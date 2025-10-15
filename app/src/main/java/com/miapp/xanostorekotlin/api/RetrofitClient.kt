package com.miapp.xanostorekotlin.api // Paquete donde vive el cliente Retrofit

import android.content.Context // Import para usar Context al construir interceptores dependientes de token
import com.miapp.xanostorekotlin.api.ApiConfig.authBaseUrl // Import del baseUrl de autenticación
import com.miapp.xanostorekotlin.api.ApiConfig.storeBaseUrl // Import del baseUrl de tienda/productos
import okhttp3.OkHttpClient // Cliente HTTP subyacente usado por Retrofit
import okhttp3.logging.HttpLoggingInterceptor // Interceptor de logging para depuración
import retrofit2.Retrofit // Clase principal para construir el cliente Retrofit
import retrofit2.converter.gson.GsonConverterFactory // Convertidor JSON (Gson)
import java.util.concurrent.TimeUnit // Utilidad para definir timeouts

/**
 * RetrofitClient
 * Centraliza la creación de instancias de Retrofit y OkHttp.
 */
object RetrofitClient {

    // Builder base de OkHttp configurado con logging y timeouts.
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

    // Función de ayuda que construye Retrofit con una baseUrl y un cliente OkHttp.
    private fun retrofit(baseUrl: String, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    /**
     * Fábrica para AuthService. Acepta un parámetro 'requiresAuth' para decidir si añade el token.
     */
    fun createAuthService(context: Context, requiresAuth: Boolean = false): AuthService {
        val clientBuilder = baseOkHttpBuilder()
        if (requiresAuth) {
            val tokenManager = TokenManager(context)
            clientBuilder.addInterceptor(AuthInterceptor { tokenManager.getToken() })
        }
        val client = clientBuilder.build()
        return retrofit(authBaseUrl, client).create(AuthService::class.java)
    }

    // Fábrica para ProductService (siempre con Authorization).
    fun createProductService(context: Context): ProductService {
        val tokenManager = TokenManager(context)
        val client = baseOkHttpBuilder()
            .addInterceptor(AuthInterceptor { tokenManager.getToken() })
            .build()
        return retrofit(storeBaseUrl, client).create(ProductService::class.java)
    }

    // Fábrica para UploadService (siempre con Authorization).
    fun createUploadService(context: Context): UploadService {
        val tokenManager = TokenManager(context)
        val client = baseOkHttpBuilder()
            .addInterceptor(AuthInterceptor { tokenManager.getToken() })
            .build()
        return retrofit(storeBaseUrl, client).create(UploadService::class.java)
    }

    /**
     * Crea un servicio de autenticación usando un token específico bajo demanda.
     * Esto evita problemas de timing con SharedPreferences durante el login.
     */
    fun createAuthServiceWithToken(authToken: String): AuthService {
        val client = baseOkHttpBuilder() // Reutilizamos el builder base con logging
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()
                chain.proceed(request)
            }
            .build()

        return retrofit(authBaseUrl, client).create(AuthService::class.java) // <-- ¡CORREGIDO!
    }
}
