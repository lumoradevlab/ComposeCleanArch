plugins {
    id("composearch.android.library")
    id("composearch.android.compose")
}

android {
    namespace = "dev.lumora.composearch.core.designsystem"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    api(libs.bundles.compose)
    api(libs.compose.foundation)
    api(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
}
