package com.miapp.xanostorekotlin.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.api.TokenManager
import com.miapp.xanostorekotlin.databinding.ActivityRegisterBinding
import com.miapp.xanostorekotlin.model.ApiError
import com.miapp.xanostorekotlin.model.AuthResponse
import com.miapp.xanostorekotlin.model.RegisterRequest
import com.miapp.xanostorekotlin.ui.user.HomeUserActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        binding.btnCreateAccount.setOnClickListener {
            validateAndRegister()
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateAndRegister() {
        val name = binding.etName.text?.toString()?.trim()
        val email = binding.etEmail.text?.toString()?.trim()
        val password = binding.etPassword.text?.toString()?.trim()

        var isValid = true
        if (name.isNullOrBlank()) {
            binding.tilName.error = "El nombre es obligatorio"
            isValid = false
        } else {
            binding.tilName.error = null
        }
        if (email.isNullOrBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Ingresa un email válido"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }
        if (password.isNullOrBlank() || password.length < 6) {
            binding.tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }
        if (!isValid) return

        binding.progress.visibility = View.VISIBLE
        binding.btnCreateAccount.isEnabled = false
        binding.tvBackToLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val appContext = applicationContext
                val authServicePublic = RetrofitClient.createAuthService(appContext)

                // 1. REGISTRO
                val request = RegisterRequest(name = name!!, email = email!!, password = password!!)
                val registerResponse: AuthResponse = withContext(Dispatchers.IO) {
                    authServicePublic.register(request)
                }
                val authToken = registerResponse.authToken

                // 2. GUARDADO DIRECTO CON ROL "user"
                // Ya no necesitamos obtener el perfil, asignamos el rol directamente.
                tokenManager.saveAuth(
                    token = authToken,
                    userName = name,
                    userEmail = email,
                    userRole = "user" // Asignamos "user" por defecto a todos los registros
                )

                Toast.makeText(this@RegisterActivity, "¡Registro exitoso! Bienvenido, $name", Toast.LENGTH_LONG).show()

                // 3. NAVEGACIÓN DIRECTA A LA PANTALLA DE USUARIO
                goToUserHome()

            } catch (e: Exception) {
                tokenManager.clear() // Limpiar cualquier dato de sesión si hay error
                Log.e("RegisterActivity", "Error en el registro", e)
                val errorMessage = when (e) {
                    is HttpException -> {
                        try {
                            val errorBody = e.response()?.errorBody()?.string()
                            val apiError = Gson().fromJson(errorBody, ApiError::class.java)
                            apiError.message ?: "Error HTTP ${e.code()}"
                        } catch (jsonError: Exception) {
                            "Error HTTP ${e.code()}"
                        }
                    }
                    else -> e.message ?: "Ocurrió un error inesperado."
                }
                Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
            } finally {
                binding.progress.visibility = View.GONE
                binding.btnCreateAccount.isEnabled = true
                binding.tvBackToLogin.isEnabled = true
            }
        }
    }

    /**
     * Navega directamente a la pantalla de usuario y limpia el historial.
     */
    private fun goToUserHome() {
        val intent = Intent(this, HomeUserActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
