package dev.roshana.buildsrc

object Dependencies {

    object CommonLibs {
        const val coroutines =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        const val arrowCore = "io.arrow-kt:arrow-core:${Versions.arrow}"
        const val arrowSyntax = "io.arrow-kt:arrow-syntax:${Versions.arrow}"
        const val arrowMeta = "io.arrow-kt:arrow-meta:${Versions.arrow}"
        const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
        const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
        const val daggerAndroid = "com.google.dagger:dagger-android-support:${Versions.dagger}"
        const val daggerAndroidCompiler =
            "com.google.dagger:dagger-android-processor:${Versions.dagger}"
        const val hilt = "com.google.dagger:hilt-android:${Versions.hilt}"
        const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
        const val gson = "com.google.code.gson:gson:${Versions.gson}"
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
        const val okHttpInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
        const val kotlinSerialConverter =
            "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.kotlinConverter}"
        const val jsonSerialization =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.jsonSerialization}"

        const val stetho = "com.facebook.stetho:stetho:${Versions.stetho}"
        const val stetho_OkHttp = "com.facebook.stetho:stetho-okhttp3:${Versions.stetho}"
        const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
        const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    }

    object ComposeLibs {
        const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        const val composeUi = "androidx.compose.ui:ui:${Versions.composeVersion}"
        const val composeMaterial = "androidx.compose.material:material:${Versions.composeVersion}"
        const val composeUiPreview =
            "androidx.compose.ui:ui-tooling-preview:${Versions.composeVersion}"
        const val composeActivity = "androidx.activity:activity-compose:${Versions.composeActivity}"
        const val composeAnimation =
            "androidx.compose.animation:animation:${Versions.composeVersion}"
        const val composeViewModel =
            "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.composeViewModel}"
        const val androidxLifeCycle =
            "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifeCycle}"
    }

    object TestLibs {
        const val junit = "junit:junit:${Versions.junit}"
        const val extJunit = "androidx.test.ext:junit:${Versions.extJunit}"
        const val testRunner = "androidx.test:runner:${Versions.testRunner}"
        const val mockitoKotlin = "com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}"
        const val archTesting = "androidx.arch.core:core-testing:${Versions.archTest}"
        const val extJUnit = "androidx.test.ext:junit:1.1.2"
        const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
        const val composeUiJunit = "androidx.compose.ui:ui-test-junit4:${Versions.composeVersion}"
        const val composeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.composeVersion}"
        const val composeUiTestManifest =
            "androidx.compose.ui:ui-test-manifest:${Versions.composeVersion}"
    }


}