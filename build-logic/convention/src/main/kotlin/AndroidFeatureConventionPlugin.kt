import com.composearch.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * `composearch.android.feature` — everything a feature module needs in one id: Android
 * library + Compose + Hilt, plus the common feature dependencies (`:core:ui`,
 * `:core:network`, navigation). A feature's own build file then only declares its
 * namespace and any extra deps.
 *
 * This template ships with NO feature modules (all UI lives in `:app`); the plugin is
 * here for the day a screen set grows big enough to earn its own module.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("composearch.android.library")
            apply("composearch.android.compose")
            apply("composearch.android.hilt")
        }
        dependencies {
            "implementation"(project(":core:ui"))
            "implementation"(project(":core:network"))
            "implementation"(platform(libs.findLibrary("androidx-compose-bom").get()))
            "implementation"(libs.findLibrary("compose-runtime").get())
            "implementation"(libs.findLibrary("androidx-navigation-compose").get())
            "implementation"(libs.findLibrary("hilt-navigation-compose").get())
            "implementation"(libs.findLibrary("androidx-lifecycle-runtime-compose").get())
        }
    }
}
