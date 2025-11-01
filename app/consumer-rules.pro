# Consumer ProGuard rules for WeatherSDK
# These rules will be applied to consumers of this library

-keep class com.lusa.weathersdk.** { *; }
-keepclassmembers class com.lusa.weathersdk.** { *; }

# Keep Retrofit interfaces
-keep interface com.lusa.weathersdk.api.** { *; }

# Keep model classes for Gson
-keep class com.lusa.weathersdk.model.** { *; }
-keepclassmembers class com.lusa.weathersdk.model.** {
    <fields>;
}

# Gson serialization
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**

