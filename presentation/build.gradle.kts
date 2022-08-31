import dev.roshana.buildsrc.BuildTypes
import dev.roshana.buildsrc.ConfigData
import dev.roshana.buildsrc.Dependencies

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    compileSdk = ConfigData.compileSdkVersion

    defaultConfig {
        minSdk = ConfigData.minSdkVersion
        targetSdk = ConfigData.targetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(Dependencies.ComposeLibs.coreKtx)

    /** compose
     **/
    implementation(Dependencies.ComposeLibs.composeUi)
    implementation(Dependencies.ComposeLibs.composeMaterial)
    implementation(Dependencies.ComposeLibs.composeUiPreview)
    implementation(Dependencies.ComposeLibs.androidxLifeCycle)
    implementation(Dependencies.ComposeLibs.composeActivity)
    implementation(Dependencies.ComposeLibs.composeHilt)
    implementation(Dependencies.ComposeLibs.composeNav)
    implementation(Dependencies.ComposeLibs.composePaging)


    /** hilt - dagger
     **/
    implementation(Dependencies.CommonLibs.daggerAndroid)
    kapt(Dependencies.CommonLibs.daggerCompiler)
    kapt(Dependencies.CommonLibs.daggerAndroidCompiler)
    implementation(Dependencies.CommonLibs.hilt)
    kapt(Dependencies.CommonLibs.hiltCompiler)
    implementation(Dependencies.CommonLibs.hiltViewModel)
    kapt(Dependencies.CommonLibs.hiltViewModelKapt)

    /** Coroutine
     **/
    implementation(Dependencies.CommonLibs.coroutines)
    implementation(Dependencies.CommonLibs.coroutinesAndroid)
    implementation(Dependencies.CommonLibs.coroutineAdapter)

    /** retrofit, okHttp
     **/
    implementation(Dependencies.CommonLibs.retrofit)
    implementation(Dependencies.CommonLibs.retrofitGson)
    implementation(Dependencies.CommonLibs.okHttpInterceptor)
    implementation(Dependencies.CommonLibs.gson)
    implementation(Dependencies.CommonLibs.kotlinSerialConverter)
    implementation(Dependencies.CommonLibs.jsonSerialization)
}