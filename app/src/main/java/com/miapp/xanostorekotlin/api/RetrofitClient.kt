package com.miapp.xanostorekotlin.api

import android.content.Context
import com.miapp.xanostorekotlin.api.ApiConfig.authBaseUrl
import com.miapp.xanostorekotlin.api.ApiConfig.eCommerceBaseUrl
import com.miapp.xanostorekotlin.api.auth.AuthInterceptor
import com.miapp.xanostorekotlin.api.auth.AuthService
import com.miapp.xanostorekotlin.api.auth.TokenManager
import com.miapp.xanostorekotlin.api.eCommerce.ECommerceService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private fun baseOkHttpBuilder(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
    }

    private fun retrofit(baseUrl: String, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // For login and register
    fun createAuthService(context: Context): AuthService {
        val client = baseOkHttpBuilder().build()
        return retrofit(authBaseUrl, client).create(AuthService::class.java)
    }

    // For getMe and ECommerceService
    private fun createAuthenticatedClient(context: Context): OkHttpClient {
        val tokenManager = TokenManager(context)
        return baseOkHttpBuilder()
            .addInterceptor(AuthInterceptor { tokenManager.getToken() })
            .build()
    }

    fun createAuthenticatedAuthService(context: Context): AuthService {
        val client = createAuthenticatedClient(context)
        return retrofit(authBaseUrl, client).create(AuthService::class.java)
    }

    fun createECommerceService(context: Context): ECommerceService {
        val client = createAuthenticatedClient(context)
        return retrofit(eCommerceBaseUrl, client).create(ECommerceService::class.java)
    }
}
