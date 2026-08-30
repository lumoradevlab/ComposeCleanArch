plugins {
    id("composearch.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.lumora.composearch.core.database"
}

dependencies {
    api(project(":core:model"))

    // Room = the persistent (offline) cache tier behind the query layer's
    // source-of-truth. This module owns only SHARED Room infra (converters,
    // conventions). Each feature declares its own @Database/@Entity/@Dao so core
    // never depends on a feature's tables.
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
