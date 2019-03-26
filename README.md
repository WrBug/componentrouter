# ComponentRouter 

一款组件间通信的方案，0反射，仅需简单配置，即可方便的进行组件间通信


### 使用

#### 添加依赖

最新版本：1.0.0

 工程根目录`build.gradle`下添加：

``` 
dependencies {
    classpath "com.wrbug.componentrouter:gradle:$version"
}
```

各模块`build.gradle`添加

```
//仅在主工程添加即可
apply plugin: 'com.wrbug.componentroutergradle'
//============
implementation "com.wrbug.componentrouter:componentrouter:$version"
annotationProcessor "com.wrbug.componentrouter:compile:$version"
```

#### 使用(以sample为例)

##### 注册Service(AFragment提供给外部使用)

```
//注册服务
@ObjectRoute("/a/AFragment")
public class AFragment extends Fragment {
    private EditText et;

    //提供给外部实例化的构造方法
    @ConstructorRouter
    public AFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a, container, false);
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

```
ComponentRouterInstance build=ComponentRouter.build("/a/AFragment");
//获取实例
Fragment fragment = build.getInstance();
if (fragment != null) {
    getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss();
}

//调用服务
String text = build.getProxy().call("getText");
```


### ComponentRouter原理

正在补充

