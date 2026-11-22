package com.scet.garagex_application.data.remote

import android.content.Context
import com.scet.garagex_application.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient creates and provides a configured Retrofit instance.
 *
 * Why we use Interceptors:
 * 1. Logging Interceptor: Prints JSON request and response bodies in Logcat so we can debug.
 * 2. Auth Interceptor: Automatically injects "Authorization: Bearer <token>" into headers.
 * 3. Dynamic Base URL Interceptor: Allows the user to switch backend IP address from the app!
 */
object RetrofitClient {

    @Volatile
    private var apiServiceInstance: GarejXApiService? = null

    @Volatile
    private var globalTokenManager: TokenManager? = null

    fun initialize(context: Context) {
        val appCtx = context.applicationContext
        globalTokenManager = TokenManager(appCtx)
        getApiService(appCtx)
    }

    /**
     * Globally accessible property for screens and viewmodels
     */
    val apiService: GarejXApiService
        get() {
            return apiServiceInstance ?: synchronized(this) {
                val client = buildOkHttpClient(globalTokenManager)
                val retrofit = Retrofit.Builder()
                    .baseUrl(TokenManager.DEFAULT_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val instance = retrofit.create(GarejXApiService::class.java)
                apiServiceInstance = instance
                instance
            }
        }

    /**
     * Returns the singleton GarejXApiService instance.
     */
    fun getApiService(context: Context): GarejXApiService {
        return apiServiceInstance ?: synchronized(this) {
            val tokenManager = TokenManager(context.applicationContext)
            globalTokenManager = tokenManager
            val client = buildOkHttpClient(tokenManager)
            val retrofit = Retrofit.Builder()
                // Default base URL fallback
                .baseUrl(TokenManager.DEFAULT_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val instance = retrofit.create(GarejXApiService::class.java)
            apiServiceInstance = instance
            instance
        }
    }

    private fun buildOkHttpClient(tokenManager: TokenManager?): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Interceptor that dynamically swaps the host/port if a custom URL is configured,
        // and injects the JWT token header if one exists.
        val dynamicAndAuthInterceptor = Interceptor { chain ->
            var request = chain.request()

            if (tokenManager != null) {
                // 1. Check if user configured a custom server URL in DataStore
                val activeBaseUrl = runBlocking {
                    tokenManager.getServerUrl().first()
                }
                val customHttpUrl = activeBaseUrl.toHttpUrlOrNull()
                if (customHttpUrl != null) {
                    val newUrl = request.url.newBuilder()
                        .scheme(customHttpUrl.scheme)
                        .host(customHttpUrl.host)
                        .port(customHttpUrl.port)
                        .build()
                    request = request.newBuilder().url(newUrl).build()
                }

                // 2. Attach Authorization Bearer token if logged in
                val token = runBlocking {
                    tokenManager.getToken().first()
                }
                if (!token.isNullOrBlank()) {
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }
            }

            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(dynamicAndAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}
