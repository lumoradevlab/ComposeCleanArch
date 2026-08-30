import java.util.Properties

plugins {
    id("composearch.android.application")
    id("composearch.android.compose")
    id("composearch.android.hilt")
    // @Serializable navigation route types (app/navigation/Routes.kt) need it.
    alias(libs.plugins.kotlin.serialization)
}

/**
 * The API key is read from local.properties (git-ignored) and injected as a
 * BuildConfig field — never committed to source. Copy local.properties.example to
 * local.properties and add your own key; a missing key builds fine and surfaces as a
 * 401 at runtime.
 */
val apiKey: String = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}.getProperty("NEWS_API_KEY").orEmpty()

android {
    namespace = "dev.lumora.composearch.app"

    defaultConfig {
        applicationId = "dev.lumora.composearch"
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField("String", "API_KEY", "\"$apiKey\"")

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // :core:ui transitively exposes :core:query, :core:designsystem and :core:datastore.
    implementation(project(":core:ui"))

    // The app is the composition root: it supplies NetworkConfig + TokenRefresher.
    implementation(project(":core:network"))
    implementation(project(":core:model"))

    // Navigation: :app owns the single NavHost and the type-safe route types.
    implementation(libs.androidx.navigation.compose)
    // hiltViewModel() — how a hook reaches the shared HookProvider.
    implementation(libs.hilt.navigation.compose)
    // Runtime for the @Serializable route types (the serialization plugin needs it).
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.coil.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    // Draws the branded launch screen and hands off to the first composed frame.
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.bundles.compose)

    debugImplementation(libs.compose.ui.tooling)
    // Heap watcher, debug builds only. No code needed — LeakCanary installs itself via
    // a ContentProvider and reports retained instances after each screen is navigated.
    debugImplementation(libs.leakcanary.android)
}

// LeakCanary is referenced by no source (it auto-installs), so dependency-analysis
// would otherwise flag it as an unused dependency and fail `buildHealth`.
dependencyAnalysis {
    issues {
        onUnusedDependencies {
            exclude("com.squareup.leakcanary:leakcanary-android")
        }
    }
}
