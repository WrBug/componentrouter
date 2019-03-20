package com.wrbug.componentroutergradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin


abstract class BasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        try {
            println("running......")
            if (project.getPlugins().hasPlugin(AppPlugin)) {
                onAppApply(project)
            } else if (project.getPlugins().hasPlugin(LibraryPlugin)) {
                onLibraryApply(project)
            }
        } catch (Exception e) {
            e.printStackTrace()
        }

    }

    void onAppApply(Project project) {

    }

    void onLibraryApply(Project project) {

    }

}