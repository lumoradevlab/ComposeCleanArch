plugins {
    id("composearch.android.library")
    id("composearch.android.hilt")
}

android {
    namespace = "dev.lumora.composearch.core.common"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
