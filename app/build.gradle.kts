import dev.roshana.buildsrc.BuildTypes
import dev.roshana.buildsrc.ConfigData
import dev.roshana.buildsrc.Dependencies

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
        implementation(Dependencies.coreKtx)
        implementation(Dependencies.composeUi)
        implementation(Dependencies.composeMaterial)
        implementation(Dependencies.composeUiPreview)
        implementation(Dependencies.androidxLifeCycle)
        implementation(Dependencies.composeActivity)

        testImplementation(Dependencies.junit)
        androidTestImplementation(Dependencies.extJunit)
        androidTestImplementation(Dependencies.espresso)
        androidTestImplementation(Dependencies.composeUiJunit)
        debugImplementation(Dependencies.composeUiTooling)
        debugImplementation(Dependencies.composeUiTestManifest)

        /*hilt*/
        implementation(Dependencies.hilt)
        kapt(Dependencies.hiltCompiler)

    }

}