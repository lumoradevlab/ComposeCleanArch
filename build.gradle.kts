import dev.roshana.buildsrc.*

plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.7.10" apply false

}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val compose_version by extra("1.3.0")
    val kotlin_version by extra("1.7.10")

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.42")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}



/*buildscript {
    val compose_version by extra("1.1.0-beta01")
    val kotlin_version by extra("1.5.31")

    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        //classpath(BuildPlugins.hiltPlugin)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.5.31" apply false
    id("org.jetbrains.kotlin.jvm") version "1.5.31" apply false
}*/

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}