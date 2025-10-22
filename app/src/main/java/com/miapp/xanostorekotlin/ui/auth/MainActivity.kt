package com.miapp.xanostorekotlin.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.auth.TokenManager
import com.miapp.xanostorekotlin.databinding.ActivityMainBinding
import com.miapp.xanostorekotlin.model.auth.LoginRequest
import com.miapp.xanostorekotlin.ui.admin.HomeAdminActivity
import com.miapp.xanostorekotlin.ui.user.HomeUserActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        if (tokenManager.isLoggedIn()) {
            val role = tokenManager.getUserRole()
            if (!role.isNullOrBlank()) {
                goToRoleBasedHome(role)
                return
            } else {
                tokenManager.clear()
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Completa email y password", Toast.LENGTH_SHORT).show()
            return
        }

        setUiEnabled(false)

        lifecycleScope.launch {
            try {
                // Step 1: Login with public service
                val authService = RetrofitClient.createAuthService(applicationContext)
                val loginResponse = withContext(Dispatchers.IO) {
                    authService.login(LoginRequest(email = email, password = password))
                }
                val authToken = loginResponse.authToken

                // Step 2: Save the token so the authenticated service can use it
                tokenManager.saveAuth(token = authToken, userName = "", userEmail = email, userRole = "")

                // Step 3: Get user profile with authenticated service
                val authenticatedAuthService = RetrofitClient.createAuthenticatedAuthService(applicationContext)
                val meResponse = withContext(Dispatchers.IO) {
                    authenticatedAuthService.getMe()
                }

                val userProfile = meResponse
                val userRole = userProfile?.role
                Log.d("LoginDebug", "Respuesta de /me: ${userProfile}\n" +
                        "meResponse: ${meResponse}")

                if (userProfile == null || userRole.isNullOrBlank()) {
                    Log.w("LoginDebug", "¡FALLO DE VALIDACIÓN! El perfil o el rol es nulo/vacío. (${userRole})")
                    tokenManager.clear()
                    throw Exception("Esta cuenta no tiene rol asignado o no se pudo obtener el perfil")
                }

                // Step 4: Save all user info
                tokenManager.saveAuth(
                    token = authToken,
                    userName = userProfile.name ?: "Usuario",
                    userEmail = userProfile.email ?: email,
                    userRole = userRole
                )

                Toast.makeText(this@MainActivity, "¡Bienvenido, ${tokenManager.getUserName()}!", Toast.LENGTH_SHORT).show()
                goToRoleBasedHome(userRole)

            } catch (e: Exception) {
                tokenManager.clear()
                Log.e("MainActivity", "Error en el proceso de login", e)

                val errorMessage = when (e) {
                    is HttpException -> when (e.code()) {
                        401 -> "Email o contraseña incorrectos."
                        403 -> "No tienes permiso para acceder. Contacta con el administrador."
                        else -> "Error de red: ${e.code()}"
                    }
                    is java.net.UnknownHostException -> "No se pudo conectar al servidor. Revisa tu conexión a internet."
                    else -> e.message ?: "Ocurrió un error inesperado."
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                setUiEnabled(true)

            }
        }
    }


    private fun goToRoleBasedHome(role: String) {
        val intent = when (role) {
            "admin" -> Intent(this, HomeAdminActivity::class.java)
            "user" -> Intent(this, HomeUserActivity::class.java)
            else -> {
                Log.e("Navigation", "Intento de navegar con rol inválido: $role")
                tokenManager.clear()
                Toast.makeText(this, "Rol de usuario no reconocido. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show()
                null
            }
        }

        if (intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setUiEnabled(isEnabled: Boolean) {
        binding.progress.visibility = if (isEnabled) View.GONE else View.VISIBLE
        binding.btnLogin.isEnabled = isEnabled
        binding.btnRegister.isEnabled = isEnabled
    }
}
