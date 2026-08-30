import com.android.build.gradle.LibraryExtension
import com.composearch.convention.configureDependencyAnalysis
import com.composearch.convention.configureDetekt
import com.composearch.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** `composearch.android.library` — base Android library: SDK config, Java/jvmTarget, detekt. */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
        }
        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
        }
        configureDetekt()
        configureDependencyAnalysis()
    }
}
