import com.android.build.api.dsl.ApplicationExtension
import com.composearch.convention.configureDependencyAnalysis
import com.composearch.convention.configureDetekt
import com.composearch.convention.configureFlavors
import com.composearch.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** `composearch.android.application` — the app module: SDK config, BuildConfig, flavors, detekt. */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }
        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = 34
            buildFeatures.buildConfig = true
            configureFlavors(this)
        }
        configureDetekt()
        configureDependencyAnalysis()
    }
}
