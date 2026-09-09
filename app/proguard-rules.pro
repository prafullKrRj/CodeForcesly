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

# Retrofit reads suspend continuation generic signatures and HTTP annotations at runtime.
-keepattributes Signature,InnerClasses,EnclosingMethod,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault
-keep interface com.prafullkumar.codeforcesly.**ApiService { *; }
-keep class com.prafullkumar.codeforcesly.common.model.** { *; }
-keep class com.prafullkumar.codeforcesly.contests.domain.models.** { *; }
-keep class com.prafullkumar.codeforcesly.problem.domain.model.** { *; }
-keep interface kotlin.coroutines.Continuation { *; }
