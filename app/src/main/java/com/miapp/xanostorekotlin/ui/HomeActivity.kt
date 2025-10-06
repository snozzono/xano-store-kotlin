package com.miapp.xanostorekotlin.ui // Paquete de la Activity principal de la app

import android.os.Bundle // Import para ciclo de vida de Activity y estado
import androidx.appcompat.app.AppCompatActivity // Import de la Activity base con compatibilidad
import androidx.fragment.app.Fragment // Import de la clase Fragment (para transacciones)
import com.miapp.xanostorekotlin.api.TokenManager // Import de nuestra clase para gestionar token/usuario
import com.miapp.xanostorekotlin.databinding.ActivityHomeBinding // Import del ViewBinding del layout activity_home.xml
import com.miapp.xanostorekotlin.ui.fragments.AddProductFragment // Import del fragmento para agregar productos
import com.miapp.xanostorekotlin.ui.fragments.ProductsFragment // Import del fragmento que lista productos
import com.miapp.xanostorekotlin.ui.fragments.ProfileFragment // Import del fragmento de perfil

/**
 * HomeActivity
 *
 * Explicación:
 * - Muestra un saludo con el nombre del usuario logeado.
 * - Contiene un BottomNavigationView para navegar entre 3 fragments:
 *   Perfil, Productos y Agregar Producto.
 * - No usamos Navigation Component para mantenerlo sencillo; hacemos transacciones manuales.
 */
class HomeActivity : AppCompatActivity() { // Declaramos la Activity Home, que gestiona los fragments

    private lateinit var binding: ActivityHomeBinding // Referencia al ViewBinding para acceder a vistas
    private lateinit var tokenManager: TokenManager // Manejador de token y datos de usuario

    override fun onCreate(savedInstanceState: Bundle?) { // Métodoo de ciclo de vida: se llama al crear la Activity
        super.onCreate(savedInstanceState) // Llamamos a la implementación base
        binding = ActivityHomeBinding.inflate(layoutInflater) // Inflamos el layout a través de ViewBinding
        setContentView(binding.root) // Establecemos la vista raíz del binding como contenido de la Activity

        tokenManager = TokenManager(this) // Inicializamos el TokenManager con el contexto de la Activity
        binding.tvWelcome.text = "Bienvenido ${tokenManager.getUserName()}" // Mostramos saludo con el nombre del usuario

        // Cargamos inicialmente el fragmento de Productos
        replaceFragment(ProductsFragment()) // Reemplazamos el contenedor por el fragmento de productos

        binding.bottomNav.setOnItemSelectedListener { item -> // Listener para navegación inferior
            when (item.itemId) { // Decidimos qué fragment mostrar según el ítem
                com.miapp.xanostorekotlin.R.id.nav_profile -> replaceFragment(ProfileFragment()) // Ir al perfil
                com.miapp.xanostorekotlin.R.id.nav_products -> replaceFragment(ProductsFragment()) // Ir a productos
                com.miapp.xanostorekotlin.R.id.nav_add -> replaceFragment(AddProductFragment()) // Ir a agregar producto
            }
            true // Devolvemos true para indicar que el evento fue manejado
        }
    }

    private fun replaceFragment(fragment: Fragment) { // Función auxiliar para reemplazar el fragment actual
        supportFragmentManager.beginTransaction() // Iniciamos una transacción de fragmentos
            .replace(binding.fragmentContainer.id, fragment) // Reemplazamos el contenedor con el fragmento dado
            .commit() // Confirmamos la transacción
    }
}