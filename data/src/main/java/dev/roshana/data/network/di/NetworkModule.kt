package dev.roshana.data.network.di

import android.app.Application
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.roshana.data.BuildConfig
import dev.roshana.data.network.api.ApiService
import dev.roshana.data.network.utils.BASE_URL
import dev.roshana.domain.R.string
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    internal fun provideOkHttpClient(application: Application) = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addRoshanaInterceptor(application)
        .addLoggerInterceptor()
        .build()


    /* @Provides
     @Singleton
     fun provideGson(): Gson = GsonFactory.instance.singletonGson*/


    @Provides
    @Singleton
    internal fun provideRetrofit(application: Application, okHttpClient: OkHttpClient, gson: Gson) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .build()


    @Provides
    @Singleton
    internal fun provideApiService(retrofit: Retrofit) =
        retrofit.create(ApiService::class.java)


    /*
    other api services
    @Provides
     @Singleton
     internal fun provideTestService(retrofit: Retrofit) =
         retrofit.create(TestService::class.java)*/


    private fun OkHttpClient.Builder.addLoggerInterceptor() = apply {
        if (BuildConfig.DEBUG) {
            addNetworkInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }

    private fun OkHttpClient.Builder.addRoshanaInterceptor(application: Application) = apply {
        addInterceptor { chain ->
            try {
                handleChain(chain)
            } catch (ioException: UnknownHostException) {
                throw  IOException(application.getString(string.connectionError), ioException)
            } catch (ioException: IOException) {
                throw  IOException(
                    application.getString(string.socketError),
                    ioException
                )
            }
        }
    }

    private fun handleChain(chain: Interceptor.Chain): Response {
        return chain.request()
            .newBuilder()
            //.addGlobalHeaders()
            //.addToken()
            .build().let {
                chain.proceed(it)
            }
    }

    /* private fun Request.Builder.addGlobalHeaders() = apply {
         globalHeaders.forEach { (key, value) ->
             addHeader(key, value)
         }
     }

     private fun Request.Builder.addToken() = apply {
        getToken().takeIf { it.isNotEmpty() }?.let {
             addHeader("Authorization", "Token $it")
         }
     }*/
}
