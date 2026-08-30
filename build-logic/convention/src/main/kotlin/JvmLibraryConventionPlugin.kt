import com.composearch.convention.configureDependencyAnalysis
import com.composearch.convention.configureDetekt
import com.composearch.convention.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

/** `composearch.jvm.library` — pure-Kotlin module (no Android), for domain/test code. */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.jvm")
        configureKotlinJvm()
        configureDetekt()
        configureDependencyAnalysis()
    }
}
