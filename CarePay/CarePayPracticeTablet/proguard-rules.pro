# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Sharath_Rampally\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-dontobfuscate
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.newrelic.** { *; }
-dontwarn com.newrelic.**
-keepattributes Exceptions, Signature, InnerClasses, LineNumberTable

-keep class org.apache.** { *; }
-dontwarn org.apache.**

-keep class com.fasterxml.** { *; }
-dontwarn com.fasterxml.**

-keep class com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-keep class java.nio.** { *; }
-dontwarn okio.**

-keep class org.joda.** { *; }
-dontwarn org.joda.**

-keep class java.lang.management.** { *; }
-dontwarn org.junit.**

-keep class com.google.code.rome.android.repackaged.com.sun.syndication.** { *; }
-keep class org.simpleframework.xml.** { *; }
-dontwarn org.springframework.http.converter.**

-keep class java.lang.invoke.** { *; }
-dontwarn retrofit2.**

-keep class junit.framework.** { *; }
-dontwarn android.test.**

-keep class edu.umd.cs.findbugs.annotations.** { *; }
-dontwarn com.clover.core.internal.calc.**

-keep class android.util.** { *; }
-dontwarn com.viewpagerindicator.**

-keep class org.w3c.dom.bootstrap.** { *; }
-dontwarn org.codehaus.jackson.**

-keep class com.google.android.gms.measurement.** { *; }
-dontwarn com.google.firebase.messaging.**

-keep class com.smartystreets.api.** { *; }

-keep class sun.misc.** { *; }
-dontwarn com.google.common.**

#Prevent crashes in App
-keep class com.google.** { *; }
-keep class com.carecloud.carepaylibray.medications.models.** { *; }

# SugarRecord
-keep class com.carecloud.carepay.service.library.dtos.** { *; }
-keep class com.carecloud.carepay.practice.library.payments.models.** { *; }


