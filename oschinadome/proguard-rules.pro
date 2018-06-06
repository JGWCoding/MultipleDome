

-keepattributes Signature
# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class net.oschina.app.improve.bean.** { *; }
-keep class net.oschina.app.improve..git.bean.** { *; }

-keepattributes EnclosingMethod

##---------------End: proguard configuration for Gson  ----------

-keep class net.oschina.app.** { *; }
-keep class net.oschina.common.** { *; }


-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-dontwarn com.thoughtworks.xstream.**
-keep class com.thoughtworks.xstream.** { *; }

-dontwarn com.makeramen.roundedimageview.**
-keep class com.makeramen.roundedimageview.RoundedTransformationBuilder

-dontwarn com.tencent.weibo.sdk.android.**
-keep class com.tencent.weibo.sdk.android.** { *; }

-dontwarn com.squareup.leakcanary.DisplayLeakService
-keep class com.squareup.leakcanary.DisplayLeakService

-dontwarn android.widget.**
-keep class android.widget.** {*;}

-dontwarn android.support.v7.widget.**
-keep class android.support.v7.widget.**{*;}

-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.tencent.weibo.sdk.**

-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.tencent.**
-keep public class javax.**
-keep public class android.webkit.**
-keep public class com.tencent.** {*;}

#open
-keep class net.oschina.open.**{*;}

-keep class com.sina.weibo.** {*;}
-keep class com.tencent.mm.sdk.** {*;}
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

# baidu map sdk
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

# Glide Start
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
# Glide End

#-dontwarn android.net.SSLCertificateSocketFactory

# 友盟Start
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class net.oschina.app.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-dontwarn com.umeng.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep public class com.umeng.socialize.* {*;}
-keep class com.umeng.scrshot.**
-keep class com.umeng.socialize.sensor.**
-keep class pub.devrel.easypermissions.**
# 友盟End

-dontwarn okio.**
-dontwarn android.net.**
-keep class android.net.SSLCertificateSocketFactory{*;}
-dontwarn com.alipay.sdk.sys.**
-keep class com.ta.utdid2.device.UTDevice{*;}