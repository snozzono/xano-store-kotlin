package com.miapp.xanostorekotlin.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.TokenManager
import com.miapp.xanostorekotlin.databinding.ActivityMainBinding
import com.miapp.xanostorekotlin.model.LoginRequest
import com.miapp.xanostorekotlin.ui.admin.HomeAdminActivity
import com.miapp.xanostorekotlin.ui.user.HomeUserActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // Si ya hay sesión, intenta navegar directamente
        if (tokenManager.isLoggedIn()) {
            val role = tokenManager.getUserRole()
            // Validamos que el rol guardado sea válido antes de navegar
            if (!role.isNullOrBlank()) {
                goToRoleBasedHome(role)
                // Usamos return para evitar que se ejecute el resto del onCreate
                return
            } else {
                // Si el rol guardado es inválido, se limpia la sesión
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

        binding.progress.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
        binding.btnRegister.isEnabled = false

        lifecycleScope.launch {
            try {
                val appContext = applicationContext
                val publicAuthService = RetrofitClient.createAuthService(appContext)

                // 1. LOGIN
                val loginResponse = withContext(Dispatchers.IO) {
                    publicAuthService.login(LoginRequest(email = email, password = password))
                }
                val authToken = loginResponse.authToken

                // 2. GUARDADO TEMPORAL
                val prefs = appContext.getSharedPreferences("session", Context.MODE_PRIVATE)
                withContext(Dispatchers.IO) {
                    prefs.edit().putString("jwt_token", authToken).commit()
                }

                // 3. OBTENCIÓN DEL PERFIL
                val privateAuthService = RetrofitClient.createAuthService(appContext, requiresAuth = true)
                val userProfile = withContext(Dispatchers.IO) {
                    privateAuthService.getMe()
                }

                // 4. VALIDACIÓN DE ROL, GUARDADO FINAL Y NAVEGACIÓN
                val userRole = userProfile?.role
                if (userRole.isNullOrBlank()) {
                    tokenManager.clear()
                    throw Exception("Esta cuenta no tiene rol")
                }

                tokenManager.saveAuth(
                    token = authToken,
                    userName = userProfile.name ?: "Usuario",
                    userEmail = userProfile.email ?: "",
                    userRole = userRole
                )

                Toast.makeText(this@MainActivity, "¡Bienvenido, ${tokenManager.getUserName()}!", Toast.LENGTH_SHORT).show()
                goToRoleBasedHome(userRole)

            } catch (e: Exception) {
                tokenManager.clear()
                Log.e("MainActivity", "Error en el proceso de login", e)
                val errorMessage = if (e.message?.contains("HTTP 401") == true) {
                    "Email o contraseña incorrectos."
                } else {
                    e.message ?: "Ocurrió un error inesperado."
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()

            } finally {
                binding.progress.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                binding.btnRegister.isEnabled = true
            }
        }
    }

    private fun goToRoleBasedHome(role: String) {
        val intent = when (role) {
            "admin" -> Intent(this, HomeAdminActivity::class.java)
            "user" -> Intent(this, HomeUserActivity::class.java)
            else -> {
                // Este caso no debería ocurrir por la validación previa,
                // pero es una salvaguarda.
                Log.e("Navigation", "Intento de navegar con rol inválido: $role")
                tokenManager.clear()
                Toast.makeText(this, "Rol de usuario no reconocido.", Toast.LENGTH_LONG).show()
                null
            }
        }

        if (intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
