# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-ignorewarnings
-optimizationpasses 10
-dontusemixedcaseclassnames
-dontoptimize
-verbose
-overloadaggressively
-allowaccessmodification
-adaptclassstrings
-adaptresourcefilenames
-adaptresourcefilecontents

-renamesourcefileattribute P
-keepattributes SourceFile,Signature,LineNumberTable

# 排除注入的 Activity
-keep class com.fankes.tsbattery.ui.activity.parasitic.ConfigActivity

# 防止某些类被 R8 混淆 (可能是 BUG)
# FIXME: 已知问题字符串类名 (即使是常量) 也会被 R8 处理为混淆后的类名
# FIXME: 所以目前只能把不允许 R8 处理的类 keep 掉，同时在当前模块中也不会被混淆就是了
-keep class kotlin.Unit
-keep class kotlin.jvm.functions.Function0

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static *** throwUninitializedProperty(...);
    public static *** throwUninitializedPropertyAccessException(...);
}

-keep class * extends android.app.Activity
-keep class * implements androidx.viewbinding.ViewBinding {
    <init>();
    *** inflate(android.view.LayoutInflater);
}