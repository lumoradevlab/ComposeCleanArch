import dev.roshana.buildsrc.BuildTypes.RELEASE
import dev.roshana.buildsrc.ConfigData
import dev.roshana.buildsrc.Dependencies

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
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
        getByName(RELEASE) {
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

    implementation(Dependencies.ComposeLibs.coreKtx)

    /** compose
     **/
    implementation(Dependencies.ComposeLibs.composePaging)

    /** hilt - dagger
     **/
    implementation(Dependencies.CommonLibs.daggerAndroid)
    kapt(Dependencies.CommonLibs.daggerCompiler)
    kapt(Dependencies.CommonLibs.daggerAndroidCompiler)
    implementation(Dependencies.CommonLibs.hilt)
    kapt(Dependencies.CommonLibs.hiltCompiler)

    /** retrofit, okHttp
     **/
    implementation(Dependencies.CommonLibs.retrofit)
    implementation(Dependencies.CommonLibs.retrofitGson)
    implementation(Dependencies.CommonLibs.okHttpInterceptor)
    implementation(Dependencies.CommonLibs.gson)
    implementation(Dependencies.CommonLibs.kotlinSerialConverter)
    implementation(Dependencies.CommonLibs.jsonSerialization)

    /** Room
     **/
    /*implementation(Dependencies.CommonLibs.room)
    implementation(Dependencies.CommonLibs.roomPagingCompose)
    kapt(Dependencies.CommonLibs.roomCompiler)*/

    /** testing
     **/
    testImplementation(Dependencies.TestLibs.junit)
    androidTestImplementation(Dependencies.TestLibs.extJunit)
    androidTestImplementation(Dependencies.TestLibs.espresso)


}