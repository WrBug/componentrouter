package com.wrbug.componentroutergradle

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.Sets
import javassist.ClassPath
import javassist.ClassPool
import javassist.CtMethod
import javassist.CtNewMethod
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class MergeFinderTransform extends BaseTransform {

    private static final String INSTANCE_FINDER_NAME = "com/wrbug/componentrouter/ComponentRouterInstanceFinder.class"
    private static final String INSTANCE_FINDER_CLASS_NAME = "com.wrbug.componentrouter.ComponentRouterInstanceFinder"

    private static final String ROUTE_FINDER_NAME = "com/wrbug/componentrouter/ComponentRouterFinder.class"
    private static final String ROUTE_FINDER_CLASS_NAME = "com.wrbug.componentrouter.ComponentRouterFinder"
    private Project mProject

    MergeFinderTransform(Project project) {
        mProject = project
    }

    @Override
    String getName() {
        return "mergeFinder"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.ContentType> getOutputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return Sets.immutableEnumSet(
                QualifiedContent.Scope.PROJECT,
                QualifiedContent.Scope.SUB_PROJECTS)
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void safeTransform(TransformInvocation transformInvocation) {
        def outputProvider = transformInvocation.outputProvider
        def instanceFinderClassPaths = new ArrayList<File>()
        def instanceDependencyClassPaths = new ArrayList<String>()
        def routeFinderClassPaths = new ArrayList<File>()
        def routeDependencyClassPaths = new ArrayList<String>()
        Map<String, List<String>> deleteEntryMap = new HashMap<>()
        def copyList = new ArrayList<File[]>()
        transformInvocation.inputs.each { input ->
            input.jarInputs.each { jarInput ->
                String destName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }
                File dest = outputProvider.getContentLocation("${destName}_${md5Name}", jarInput.contentTypes, jarInput.scopes, Format.JAR)
                findClass(jarInput, INSTANCE_FINDER_NAME, instanceFinderClassPaths, instanceDependencyClassPaths, deleteEntryMap)
                findClass(jarInput, ROUTE_FINDER_NAME, routeFinderClassPaths, routeDependencyClassPaths, deleteEntryMap)
                File[] files = new File[2]
                files[0] = jarInput.file
                files[1] = dest
                copyList.add(files)
            }
            input.directoryInputs.each { directoryInput ->
                def dir = directoryInput.file
                mergeInstance(dir, instanceFinderClassPaths, instanceDependencyClassPaths, deleteEntryMap)
                mergeRoute(dir, routeFinderClassPaths, routeDependencyClassPaths)
                copyList.each {
                    println("copy:" + Arrays.toString(it))
                    FileUtils.copyFile(it[0], it[1])
                }
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(dir, dest)
            }
        }
    }


    void mergeRoute(File dir, List routeFinderClassPaths, List routeDependencyClassPaths) {
        if (routeFinderClassPaths.size() > 0) {
            File finderFile = findClassFile(dir, ROUTE_FINDER_NAME)
            println("mergeRoute finderFile :" + finderFile)
            if (finderFile) {
                routeFinderClassPaths.add(dir)
                def componentRouterInstanceFinderMethods = new ArrayList<CtMethod>()
                // 找到所有的 get 方法
                List<ClassPool> classPoolList = new ArrayList<>()
                List<ClassPath> classPathList = new ArrayList<>()
                routeFinderClassPaths.each { file ->
                    // ClassPool 会有缓存，所以每次都 new 一个，防止从缓存获取
                    def classPool = new ClassPool(true)
                    def classPath = classPool.insertClassPath(file.absolutePath)
                    def finderClass = classPool.get(ROUTE_FINDER_CLASS_NAME)
                    def method = finderClass.getDeclaredMethod("get")
                    if (method) {
                        componentRouterInstanceFinderMethods.add(method)
                    }
                    classPoolList.add(classPool)
                    classPathList.add(classPath)
                    classPool.removeClassPath(classPath)
                }
                if (componentRouterInstanceFinderMethods.size() > 0) {
                    try {
                        def tmp = componentRouterInstanceFinderMethods[0]
                        def classPool = new ClassPool(true)
                        classPool.insertClassPath(dir.absolutePath)
                        // 添加 android.jar
                        classPool.insertClassPath(getAndroidClassPath())
                        routeDependencyClassPaths.each { path ->
                            classPool.insertClassPath(path)
                        }
                        def clazz = classPool.get(ROUTE_FINDER_CLASS_NAME)
                        def getMethod = CtNewMethod.copy(tmp, tmp.name, clazz, null)
                        // 删除原来的 get 方法
                        clazz.removeMethod(clazz.getDeclaredMethod("get"))
                        def body = new StringBuilder()
                        body.append("{Object result = null;\n")
                        componentRouterInstanceFinderMethods.eachWithIndex { method, index ->
                            def newName = "get\$\$${index}"
                            method.setName(newName)
                            clazz.addMethod(CtNewMethod.copy(method, clazz, null))
                            body.append("result = ${method.name}(\$1);\n")
                            body.append("if (result != null) return result;\n")

                        }
                        body.append("return null;\n")
                        body.append("}\n")
                        getMethod.setBody(body.toString())
                        // 添加新的 get 方法
                        clazz.addMethod(getMethod)
                        // 把修改后的 Class 写入文件
                        clazz.writeFile(dir.absolutePath)
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


    void mergeInstance(File dir, List instanceFinderClassPaths, List instanceDependencyClassPaths, Map<String, List<String>> deleteEntryMap) {
        if (instanceFinderClassPaths.size() > 0) {
            File finderFile = findClassFile(dir, INSTANCE_FINDER_NAME)
            println("finderFile :" + finderFile)
            if (finderFile) {
                deleteEntryMap.entrySet().each {
                    deleteEntry(it.key, it.value)
                }
                deleteEntryMap.clear()
                instanceFinderClassPaths.add(dir)
                def componentRouterInstanceFinderMethods = new ArrayList<CtMethod>()
                // 找到所有的 get 方法
                List<ClassPool> classPoolList = new ArrayList<>()
                List<ClassPath> classPathList = new ArrayList<>()
                instanceFinderClassPaths.each { file ->
                    // ClassPool 会有缓存，所以每次都 new 一个，防止从缓存获取
                    def classPool = new ClassPool(true)
                    def classPath = classPool.insertClassPath(file.absolutePath)
                    def finderClass = classPool.get(INSTANCE_FINDER_CLASS_NAME)
                    def method = finderClass.getDeclaredMethod("get")
                    if (method) {
                        componentRouterInstanceFinderMethods.add(method)
                    }
                    classPoolList.add(classPool)
                    classPathList.add(classPath)
                    classPool.removeClassPath(classPath)
                }
                if (componentRouterInstanceFinderMethods.size() > 0) {
                    classPoolList.eachWithIndex { ClassPool classPool, int index ->
                        classPool.removeClassPath(classPathList.get(index))
                    }
                    try {
                        def tmp = componentRouterInstanceFinderMethods[0]

                        def classPool = new ClassPool(true)
                        classPool.insertClassPath(dir.absolutePath)
                        // 添加 android.jar
                        classPool.insertClassPath(getAndroidClassPath())
                        instanceDependencyClassPaths.each { path ->
                            classPool.insertClassPath(path)
                        }
                        def clazz = classPool.get(INSTANCE_FINDER_CLASS_NAME)
                        def getMethod = CtNewMethod.copy(tmp, tmp.name, clazz, null)
                        // 删除原来的 get 方法
                        clazz.removeMethod(clazz.getDeclaredMethod("get"))
                        def body = new StringBuilder()
                        body.append("{Object result = null;\n")
                        componentRouterInstanceFinderMethods.eachWithIndex { method, index ->
                            def newName = "get\$\$${index}"
                            method.setName(newName)
                            clazz.addMethod(CtNewMethod.copy(method, clazz, null))
                            body.append("result = ${method.name}(\$1,\$2);\n")
                            body.append("if (result != null) return result;\n")

                        }
                        body.append("return null;\n")
                        body.append("}\n")
                        getMethod.setBody(body.toString())
                        // 添加新的 get 方法
                        clazz.addMethod(getMethod)
                        // 把修改后的 Class 写入文件
                        clazz.writeFile(dir.absolutePath)
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    String getAndroidClassPath() {
        Properties properties = new Properties()
        properties.load(mProject.rootProject.file('local.properties').newDataInputStream())
        def sdkDir = properties.getProperty('sdk.dir')
        if (sdkDir == null || sdkDir.isEmpty()) {
            sdkDir = System.getenv("ANDROID_HOME")
        }
        return "${sdkDir}/platforms/${mProject.android.compileSdkVersion}/android.jar"
    }

    static void findClass(JarInput jarInput, String name, List<String> finderClassPaths, List<String> dependencyClassPaths, Map<String, List<String>> deleteEntryMap) {
        def jarFile = new JarFile(jarInput.file)
        def entry = findEntry(jarFile, name)
        if (entry) {
            def finderFile = new File(jarInput.file.parentFile, entry.name)
            finderFile.parentFile.mkdirs()
            IOUtils.copy(jarFile.getInputStream(entry), new FileOutputStream(finderFile))
            finderClassPaths.add(jarInput.file.parentFile)
            def key = jarInput.file.absolutePath
            List<String> list
            if (deleteEntryMap.containsKey(key)) {
                list = deleteEntryMap.get(key)
            } else {
                list = new ArrayList<String>()
                deleteEntryMap.put(key, list)
            }
            list.add(entry.name)
            println("find:" + key + ", name=" + name)
        } else {
            dependencyClassPaths.add(jarInput.file.absolutePath)
        }
    }

    File findClassFile(File file, String className) {
        def paths = className.split("/")
        paths.each { path ->
            file = findFile(file, path)
        }
        return file
    }

    File findFile(File dir, String name) {
        def result
        if (dir && dir.exists()) {
            dir.listFiles().each { file ->
                if (name == file.name) {
                    result = file
                }
            }
        }
        return result
    }

    static JarEntry findEntry(JarFile jarFile, String name) {
        def entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            def jarEntry = entries.nextElement()
            if (name == jarEntry.name) {
                return jarEntry
            }
        }
        return null
    }

    static void deleteEntry(String path, List<String> entryNames) {
        println("清理：" + entryNames)
        File file = new File(path)
        JarFile jarFile = new JarFile(file)
        def tmpJar = new File(file.parentFile, file.name + ".tmp")
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpJar))

        def entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            def jarEntry = entries.nextElement()
            if (entryNames.contains(jarEntry.name)) {
                println("过滤：" + jarEntry.name)
                continue
            }
            println("添加：" + jarEntry.name)
            String entryName = jarEntry.name
            ZipEntry zipEntry = new ZipEntry(entryName)
            InputStream inputStream = jarFile.getInputStream(jarEntry)
            jarOutputStream.putNextEntry(zipEntry)
            jarOutputStream.write(IOUtils.toByteArray(inputStream))
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        jarFile.close()

        if (file.exists()) {
            file.delete()
        }
        tmpJar.renameTo(file)
    }

}