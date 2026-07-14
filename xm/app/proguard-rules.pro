# ProGuard规则
-keep class com.ai.weather.data.model.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
