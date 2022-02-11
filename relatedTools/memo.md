# DroidBench-ICC

## True Negative

Warning: Empty XML file: /oracles/DroidBench-ICC/ActivityCommunication1_oracle.xml

+ 包名：de.ecspride
+ 备注：不存在 ICC

## False Negative

Warning: Empty XML file: /oracles/DroidBench-ICC/ActivityCommunication6_oracle.xml

+ 包名：edu.mit.icc_action_string_operations
+ 备注：中途调用了 List\<Intent\>.get(), 分析不到

## False Negative

Warning: Empty XML file: /oracles/DroidBench-ICC/ActivityCommunication8_oracle.xml

+ 包名：edu.mit.icc_pass_action_string_through_api
+ 备注：中途调用了 List\<String\>.get(), 分析不到

## False Negative

Warning: Empty XML file: /oracles/DroidBench-ICC/BroadcastTaintAndLeak1_oracle.xml

+ 包名：edu.mit.icc_broadcast_programmatic_intentfilter
+ 备注：在 `onCreate()` 中通过 `registerReceiver()` 动态注册了一个指定 action 的 *BroadcastReceiver*，并在 `onDestroy()` 时调用

## True Negative

Warning: Empty XML file: /oracles/DroidBench-ICC/ComponentNotInManifest1_oracle.xml

+ 包名：edu.mit.icc_component_not_in_manifest
+ 备注：目标 Activity 不在 AndroidManifest 中，无效 ICC

## True Negative

Warning: Empty XML file: /oracles/DroidBench-ICC/IntentSink1_oracle.xml

+ 包名：de.ecspride
+ 备注：不存在 ICC

## Controversial

Warning: Empty XML file: /oracles/DroidBench-ICC/IntentSink2_oracle.xml

+ 包名：de.ecspride
+ 备注：目标组件是通过 EditText 输入的类名，可以是任意值

## Controversial

Warning: Empty XML file: /oracles/DroidBench-ICC/IntentSource1_oracle.xml

+ 包名：lu.uni.snt.serval
+ 备注：在 onCreate 中通过 action `android.intent.action.MAIN` 和 `startActivityForResult()` 启动自身。这个算 ICC 吗？

## True Negative

Warning: Empty XML file: /oracles/DroidBench-ICC/SharedPreferences1_oracle.xml

+ 包名：edu.mit.shared_preferences
+ 备注：不存在 ICC

## True Negative

Warning: Empty XML file: /oracles/DroidBench-ICC/Singletons1_oracle.xml

+ 包名：edu.mit.to_components_share_memory
+ 备注：不存在 ICC

## False Negative

Warning: Empty XML file: /oracles/ICCBench/icc_dynregister1_oracle.xml

+ 来源：org.arguslab.icc_dynregister1.MainActivity
+ 目标：org.arguslab.icc_dynregister1.MyReceiver
+ 备注：`onCreate()` 时 `registerReceiver()` 动态注册了指定 action `com.fgwei` 的 *MyReceiver*，并在隐式回调 `onRequestPermissionsResult()` 中调用 `leakImei()`，`leakImei()` 中调用 `sendBroadcast()`。

## False Negative

Warning: Empty XML file: /oracles/ICCBench/icc_dynregister2_oracle.xml

+ 来源：org.arguslab.icc_dynregister2.MainActivity
+ 目标：org.arguslab.icc_dynregister2.MainActivity
+ 备注：`onCreate()` 时 `registerReceiver()` 动态注册了指定 action `com.ksu` 的 *MyReceiver*，并在隐式回调 `onRequestPermissionsResult()` 中调用 `leakImei()`，`leakImei()` 中通过 *StringBuilder* 合成 action 调用 `sendBroadcast()`。

## Duplicate

+ 文件：
+ /oracles/ICCBench/icc_explicit_src_nosink_oracle.xml
+ /oracles/ICCBench/icc_explicit_src_sink_oracle.xml
+ /oracles/ICCBench/icc_explicit1.xml
+ /oracles/ICCBench/icc_implicit_action.xml
+ 备注：各出现了两条Duplicate

## False Negative

Warning: Empty XML file: /oracles/ICCBench/icc_implicit_category_oracle.xml

+ 来源：org.arguslab.icc_implicit_category.MainActivity
+ 目标：org.arguslab.icc_implicit_category.FooActivity
+ 备注：指定了 action 为 [test], category 为 [amandroid.impliciticctest_Categories.testcategory1], 启动 FooActivity。看起来似乎没有问题，AndroidManifest 里定义也正确

# ICCBench

## False Negative

Warning: Empty XML file: /oracles/ICCBench/icc_implicit_data1_oracle.xml

+ 来源：org.arguslab.icc_implicit_data1.MainActivity
+ 目标：org.arguslab.icc_implicit_data1.FooActivity
+ 备注：通过 setData 设置的隐式 Intent

## False Negative

Warning: Empty XML file: /oracles/ICCBench/icc_implicit_data2_oracle.xml

+ 来源：org.arguslab.icc_implicit_data2.MainActivity
+ 目标：org.arguslab.icc_implicit_data2.FooActivity
+ 备注：通过 setType 设置的隐式 Intent

# RAICCBench

## 误报的边
+ 来源：lu.uni.trux.raicc.addAction2.MainActivity
+ 目标：lu.uni.trux.raicc.addAction2.TargetActivity
+ 原因：没有调用 NotificationManagerCompat.notify() 方法，没有出口。


# storyBoard

## Duplicate

+ 来源：com.test.ui.transition.MainActivity (activity_fragment_1)
+ 目标：com.test.ui.transition.NextActivity (activity_fragment_1)
+ 原因：其中一条是第一个 Fragment 直接跳转，另一条是第一个 Fragment 将自身替换为第二个 Fragment，由第二个 Fragment 完成跳转。
这个是要标两条边以强调两种不同的路径，还是只标一条？（现在标了两条）

## False Negative

+ 来源：com.test.ui.transition.AActivity (activity_fragment_2)
+ 目标：com.test.ui.transition.MainActivity (activity_fragment_2)
+ 备注：`AActivity.onCreate()` 中调用自定义方法 `setUpFragment()` 加载 *BFragment*，*BFragment* 调用自定义方法 `jump2Activity()`，后者调用 `startActivity()` 方法启动 *MainActivity*


## False Negative

+ 来源：com.test.ui.transition.AActivity (activity_fragment_2)
+ 目标：com.test.ui.transition.NextActivity (activity_fragment_2)
+ 备注：`AActivity.onCreate()` 中调用自定义方法 `setUpFragment()` 加载 *BFragment*，*BFragment* 调用自定义方法 `jump2Activity()`，后者调用 `startActivity()` 方法启动 *NextActivity*


## False Negative

+ 来源：com.test.ui.transition.NextActivity (activity_fragment_2)
+ 目标：com.test.ui.transition.AActivity (activity_fragment_2)
+ 备注：`NextActivity.onCreate()` 中调用自定义方法 `go2Activity()` 加载 *AActivity*，*Context* 是通过 `getApplicationContext()` 获取并传参的

## True Negative

Warning: Empty XML file: /oracles/StoryDroid/fragment_1_oracle.xml

+ 备注：只是 Fragment 之间来回切换，没有 ICC

## True Negative

Warning: Empty XML file: /oracles/StoryDroid/fragment_2_oracle.xml

+ 备注：只是 Fragment 之间来回切换，没有 ICC
