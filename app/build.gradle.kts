import dev.roshana.buildsrc.BuildTypes
import dev.roshana.buildsrc.ConfigData

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdk = ConfigData.compileSdkVersion

    defaultConfig {
        applicationId = ConfigData.applicationId
        minSdk = ConfigData.minSdkVersion
        targetSdk = ConfigData.targetSdkVersion
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        getByName(BuildTypes.RELEASE) {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["compose_version"] as String
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    dependencies {
        implementation("androidx.core:core-ktx:1.7.0")
        implementation("androidx.compose.ui:ui:${rootProject.extra["compose_version"]}")
        implementation("androidx.compose.material:material:${rootProject.extra["compose_version"]}")
        implementation("androidx.compose.ui:ui-tooling-preview:${rootProject.extra["compose_version"]}")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
        implementation("androidx.activity:activity-compose:1.4.0")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.3")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
        androidTestImplementation("androidx.compose.ui:ui-test-junit4:${rootProject.extra["compose_version"]}")
        debugImplementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")
        debugImplementation("androidx.compose.ui:ui-test-manifest:${rootProject.extra["compose_version"]}")
        
        implementation(dev.roshana.buildsrc.Dependencies.hilt)
        kapt(dev.roshana.buildsrc.Dependencies.hiltCompiler)

    }

}