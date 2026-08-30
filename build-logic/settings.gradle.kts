// Standalone build that hosts the convention plugins. It is pulled into the main
// build via `includeBuild("build-logic")` in the root settings, so every module
// can apply `id("composearch.android.library")` etc. with no version.
dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
