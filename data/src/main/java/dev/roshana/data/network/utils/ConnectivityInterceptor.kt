package dev.roshana.data.network.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/** PariSa;
coding and smoking ;)
 **/

class NoConnectivityException : IOException() {
    override val message: String
        get() = "عدم اتصال به اینترنت"
}

class ConnectivityInterceptor(
    val app: Application
) : Interceptor {

    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this) {
            val originalRequest = chain.request().newBuilder()
            if (!isNetworkAvailable(app)) {
                throw NoConnectivityException()
            }
            val request = requestHeader(originalRequest)
            return chain.proceed(request)
        }
    }

    companion object {
        fun requestHeader(request: Request.Builder): Request {
            return request
                .addHeader("Accept", "application/json")
                .addHeader("Content-type", "application/json")
                .build()

        }
    }

}
