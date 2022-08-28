// settings.gradle.kts
import de.fayard.refreshVersions.bootstrapRefreshVersionsForBuildSrc

buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.5")
}

bootstrapRefreshVersionsForBuildSrc()