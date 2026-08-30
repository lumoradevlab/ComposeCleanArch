pluginManagement {
    // The convention plugins (composearch.android.library, .feature, …) live here.
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "ComposeCleanArch"

include(":app")

// Core modules (shared infrastructure; no feature knows about another feature).
include(":core:model")
include(":core:common")
include(":core:network")
include(":core:datastore")
include(":core:database")
include(":core:query")
include(":core:designsystem")
include(":core:ui")
include(":core:testing")
