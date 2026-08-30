plugins {
    id("composearch.android.library")
    id("composearch.android.compose")
}

android {
    namespace = "dev.lumora.composearch.core.query"
}

dependencies {
    api(project(":core:common"))

    // The server-state cache is hand-rolled (no Store5): QueryClient + QueryEntry
    // hold the cache, dedup, TTL and invalidation; useQuery/useMutation are the
    // thin Compose call sites. Engine logic is plain Kotlin so it unit-tests fast.
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.androidx.lifecycle.runtime.ktx)      // repeatOnLifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)  // LocalLifecycleOwner
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject) // @Inject/@Singleton on QueryClient (Hilt picks it up in :app)

    // The cache/TTL/dedup logic is plain Kotlin, so it's a fast local JVM test.
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
