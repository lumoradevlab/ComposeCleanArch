package dev.roshana.data.network.di

import android.app.Application
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.roshana.data.BuildConfig
import dev.roshana.data.network.api.ApiService
import dev.roshana.data.network.utils.BASE_URL
import dev.roshana.domain.utils.getToken
import dev.roshana.domain.utils.globalHeaders
import dev.roshana.domain.R.string
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
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


/*    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient {

        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                    return arrayOf()
                }
            }
        )
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { _, _ -> true }
        //builder.addInterceptor(connectivityInterceptor)
        builder.addInterceptor(HttpLoggingInterceptor().apply {
            this.setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        builder.connectTimeout(1, TimeUnit.MINUTES)
        builder.readTimeout(1, TimeUnit.MINUTES)
        builder.writeTimeout(1, TimeUnit.MINUTES)

        return builder.build()

    }*/


    /*@ExperimentalSerializationApi
       @Singleton
       @Provides
       fun providesAPI(client: OkHttpClient): ApiService {

           return Retrofit.Builder()
               .addConverterFactory(GsonConverterFactory.create())
               .baseUrl(BASE_URL)
               .client(client)
               .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
               .build()
               .create(ApiService::class.java)
       }*/
    @Provides
    @Singleton
    internal fun provideOkHttpClient(application: Application) = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addRoshanaInterceptor(application)
        .addLoggerInterceptor()
        .build()

    @Provides
    @Singleton
    internal fun provideRetrofit(application: Application, okHttpClient: OkHttpClient) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    internal fun provideApiService(retrofit: Retrofit) =
        retrofit.create(ApiService::class.java)

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
                throw  IOException(application.getString(string.socketError), ioException)
            }
        }
    }

    private fun handleChain(chain: Interceptor.Chain): Response {
        return chain.request()
            .newBuilder()
            .addGlobalHeaders()
            .addToken()
            .build().let {
                chain.proceed(it)
            }
    }

    private fun Request.Builder.addGlobalHeaders() = apply {
        globalHeaders.forEach { (key, value) ->
            addHeader(key, value)
        }
    }

    private fun Request.Builder.addToken() = apply {
        getToken().takeIf { it.isNotEmpty() }?.let {
            addHeader("Authorization", "Token $it")
        }
    }

}
