plugins {
    id("composearch.jvm.library")
}

// Test utilities shared by all modules. Plain JVM so it stays fast.
dependencies {
    api(libs.kotlinx.coroutines.test)
    api(libs.junit)
}
