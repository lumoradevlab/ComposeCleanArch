plugins {
    id("composearch.android.library")
    id("composearch.android.hilt")
}

android {
    namespace = "dev.lumora.composearch.core.datastore"
}

dependencies {
    api(project(":core:common"))
    implementation(libs.androidx.datastore.preferences)
}
