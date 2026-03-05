# Keep kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class ru.boilercalc.app.**$$serializer { *; }
-keepclassmembers class ru.boilercalc.app.** {
    *** Companion;
}
-keepclasseswithmembers class ru.boilercalc.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
