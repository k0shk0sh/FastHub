-keepattributes SourceFile,LineNumberTable
-dontobfuscate
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
-keepattributes *Annotation*,Signature
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keepclassmembers class **.R$* {
  public static <fields>;
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers enum * { *; }
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
-keep class com.fastaccess.data.** { *; }
-keep class com.fastaccess.provider.rest.** { *; }
-keepclassmembers class com.prettifier.pretty.callback.MarkDownInterceptorInterface {
   public *;
}
-keepclassmembers enum io.requery.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class .R
-keep class **.R$* {
    <fields>;
}
-keep class net.nightwhistler.** { *; }
-keep class org.htmlcleaner.** { *; }
-keeppackagenames org.jsoup.nodes
-keep class com.github.b3er.** { *; }
-keep class com.memoizrlabs.** { *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class com.google.android.gms.** { *; }

-dontwarn com.github.b3er.**
-dontwarn com.memoizrlabs.**
-dontwarn java.lang.FunctionalInterface
-dontwarn java.util.**
-dontwarn java.time.**
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**
-dontwarn javax.cache.**
-dontwarn javax.naming.**
-dontwarn javax.transaction.**
-dontwarn java.sql.**
-dontwarn android.support.**
-dontwarn io.requery.cache.**
-dontwarn io.requery.rx.**
-dontwarn io.requery.reactivex.**
-dontwarn io.requery.reactor.**
-dontwarn io.requery.query.**
-dontwarn io.requery.android.sqlcipher.**
-dontwarn io.requery.android.sqlitex.**
-dontwarn org.jaxen.**
-dontwarn org.jdom.**
-dontwarn com.google.android.gms.**
-dontwarn android.animation.**
-dontwarn java.io.**
-dontwarn android.content.**
-dontwarn org.jdom.**
-dontwarn kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor
-dontwarn kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
-dontwarn kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptorWithTypeParameters
-dontwarn kotlin.reflect.jvm.internal.impl.descriptors.annotations.AnnotationDescriptor
-dontwarn kotlin.reflect.jvm.internal.impl.descriptors.impl.PropertyDescriptorImpl
-dontwarn kotlin.reflect.jvm.internal.impl.load.java.JavaClassFinder
-dontwarn kotlin.reflect.jvm.internal.impl.resolve.OverridingUtil
-dontwarn kotlin.reflect.jvm.internal.impl.types.DescriptorSubstitutor
-dontwarn kotlin.reflect.jvm.internal.impl.types.DescriptorSubstitutor
-dontwarn kotlin.reflect.jvm.internal.impl.types.TypeConstructor
-dontwarn java.beans.**
-dontwarn javax.**
-dontwarn lombok.**
-dontwarn java.lang.invoke.*
-dontwarn rx.**
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn org.apache.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn retrofit.**
-dontwarn retrofit2.adapter.rxjava.CompletableHelper$**
-dontwarn retrofit2.Platform$Java8
-dontwarn sun.misc.**
-dontwarn sun.misc.Unsafe
-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
-dontwarn retrofit.appengine.UrlFetchClient
-dontwarn icepick.**
-dontwarn com.fastaccess.ui.modules.repos.**
-dontwarn org.apache.xerces.parsers.**
-dontwarn oracle.xml.**
-dontwarn org.jdom.**
-dontwarn okhttp3.internal.**