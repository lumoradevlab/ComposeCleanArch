plugins {
    id("composearch.jvm.library")
}

// Pure Kotlin. Intentionally has NO Android dependencies — domain logic must be
// testable without an emulator. Do not add Android libraries here.

dependencies {
    testImplementation(libs.junit)
}
