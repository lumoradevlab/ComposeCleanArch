plugins {
    id("composearch.android.library")
    id("composearch.android.compose")
}

android {
    namespace = "dev.lumora.composearch.core.ui"
}

dependencies {
    // api: screens that use QueryContent need the query types and the design system,
    // so both are part of this module's public surface.
    api(project(":core:query"))
    api(project(":core:designsystem"))
    api(project(":core:datastore"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
