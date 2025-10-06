package com.miapp.xanostorekotlin.api // Paquete donde declaramos el interceptor

import okhttp3.Interceptor // Import de la interfaz Interceptor de OkHttp
import okhttp3.Response // Import del tipo Response de OkHttp

/**
 * Interceptor de autenticación.
 *
 * Este interceptor toma el token guardado (si existe) y lo añade
 * al header Authorization como "Bearer <token>" para todas las
 * solicitudes que pasen por el cliente de OkHttp configurado.
 * Cada línea está comentada con fines didácticos.
 */
class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor { // Clase que implementa Interceptor; recibe un proveedor de token
    override fun intercept(chain: Interceptor.Chain): Response { // Métodoo obligatorio que intercepta cada request
        val original = chain.request() // Obtenemos la solicitud original
        val token = tokenProvider() // Obtenemos el token actual (puede ser null o vacío)
        val request = if (!token.isNullOrBlank()) { // Si el token no es nulo ni vacío
            original.newBuilder() // Creamos un builder basado en la solicitud original
                .addHeader("Authorization", "Bearer $token") // Añadimos el header Authorization con el esquema Bearer
                .build() // Construimos la nueva solicitud con el header
        } else { // Si no hay token
            original // Usamos la solicitud original tal cual
        }
        return chain.proceed(request) // Continuamos la cadena con la solicitud (modificada o original)
    }
}