package dev.roshana.buildsrc

object Dependencies {
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
    const val stetho = "com.facebook.stetho:stetho:${Versions.stetho}"
    const val stetho_OkHttp = "com.facebook.stetho:stetho-okhttp3:${Versions.stetho}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
}