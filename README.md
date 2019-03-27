# ComponentRouter 

 一款高效的组件间通信方案，0反射，仅需简单配置，即可实现在组件完全解耦的情况下的通信


### 使用

#### 添加依赖

最新版本：`1.0.1`

 工程根目录`build.gradle`下添加：

``` gradle
dependencies {
    classpath "com.wrbug.componentrouter:gradle:$version"
}
```

各模块`build.gradle`添加

``` gradle
//仅在主工程添加即可 (只有一个模块使用@ObjectRoute 时不要添加！！！)
apply plugin: 'com.wrbug.componentroutergradle'
//============
implementation "com.wrbug.componentrouter:componentrouter:$version"
annotationProcessor "com.wrbug.componentrouter:compile:$version"
```

#### 使用1. (获取Fragment实例)

##### 注册Service([AFragment](a_component/src/main/java/com/wrbug/componentrouter/acomponent/AFragment.java)提供给外部使用)

``` java
//注册服务
@ObjectRoute("/a/AFragment")
public class AFragment extends Fragment {
    private EditText et;

    //提供给外部实例化的构造方法
    @ConstructorRouter
    public AFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        et = view.findViewById(R.id.et);
    }

    //提供给外部访问的方法
    @MethodRouter("getText")
    String getText() {
        return et.getText().toString();
    }

}
```

##### 获取service代理（跨组件使用Fragment）

``` java
ComponentRouterInstance build=ComponentRouter.build("/a/AFragment");
//获取实例
Fragment fragment = build.getInstance();
if (fragment != null) {
    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss();
}

//调用服务
String text = build.getProxy().call("getText");
```

#### 使用2. (跨组件调用单例)

##### 注册Service([UserManagerService](a_component/src/main/java/com/wrbug/componentrouter/acomponent/UserManagerService.java)提供sp服务给外部)

``` java
@ObjectRoute("/a/userManager")
public class UserManagerService {
    private static volatile UserManagerService instance;
    private Context mContext;
    private SharedPreferences mUserSharedPreferences;

    private UserManagerService(Context context) {
        mContext = context.getApplicationContext();
        mUserSharedPreferences = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @SingletonRouter
    public static UserManagerService getInstance(Context context) {
        if (instance == null) {
            synchronized (UserManagerService.class) {
                if (instance == null) {
                    instance = new UserManagerService(context);
                }
            }
        }
        return instance;
    }

    @MethodRouter("saveUsername")
    public void saveUsername(String username) {
        mUserSharedPreferences.edit().putString("username", username).apply();
    }

    @MethodRouter("getUsername")
    public String getUsername() {
        return mUserSharedPreferences.getString("username", "");
    }

} 
```
##### 获取service代理

``` java
// UserManagerService 存在 @SingletonRouter注解，build 也为单例
ComponentRouterInstance build = ComponentRouter.build("/a/userManager", this.getApplicationContext());
//保存username
build.getProxy().call("saveUsername", "WrBug");
//获取username
String username = build.getProxy().call("getUsername");
```

### 注解使用

##### [@ObjectRoute](component_router/src/main/java/com/wrbug/componentrouter/annotation/ObjectRoute.java)

类注解，参数path，服务类只有使用该注解，其他注解才会生效

##### [@ConstructorRouter](component_router/src/main/java/com/wrbug/componentrouter/annotation/ConstructorRouter.java)

构造方法注解，用于`ComponentRouter.build()`生成 代理实例，**服务类中如有构造方法，将需要提供给外部的构造方法加上该注解**
##### [@SingletonRouter](component_router/src/main/java/com/wrbug/componentrouter/annotation/SingletonRouter.java)

单例注解，用于获取服务类单例的静态方法，用法同`ConstructorRouter`，服务类中如使用`@SingletonRouter`，`@ConstructorRouter`将自动失效，使用参考[UserManagerService](a_component/src/main/java/com/wrbug/componentrouter/acomponent/UserManagerService.java)

##### [@MethodRouter](component_router/src/main/java/com/wrbug/componentrouter/annotation/MethodRouter.java)

方法注解，用于服务类提供给外部的方法，使用参考[UserManagerService](a_component/src/main/java/com/wrbug/componentrouter/acomponent/UserManagerService.java)


### ComponentRouter原理

正在补充

