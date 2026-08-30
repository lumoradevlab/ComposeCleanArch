plugins {
    id("composearch.android.library")
    id("composearch.android.hilt")
}

android {
    namespace = "dev.lumora.composearch.core.network"
}

dependencies {
    api(project(":core:common"))
    // Repositories expose/consume domain types, so model is part of this module's
    // public API. Declared directly rather than leaned on via :core:common.
    api(project(":core:model"))

    api(libs.retrofit.core)
    implementation(libs.bundles.retrofit)
    implementation(libs.gson)

    testImplementation(libs.junit)
}
