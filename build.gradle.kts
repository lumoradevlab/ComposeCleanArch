plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false

    // detekt is applied per-module by the convention plugins (composearch.android.* /
    // composearch.jvm.library), so it's only put on the classpath here.
    alias(libs.plugins.detekt) apply false

    // dependency-analysis is applied ONCE at the root; it auto-inspects every
    // subproject and aggregates into the `buildHealth` task (unused/undeclared deps).
    alias(libs.plugins.dependency.analysis)
}
