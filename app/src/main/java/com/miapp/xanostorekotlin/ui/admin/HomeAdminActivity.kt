package com.miapp.xanostorekotlin.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.api.TokenManager
import com.miapp.xanostorekotlin.databinding.ActivityHomeAdminBinding
import com.miapp.xanostorekotlin.ui.MainActivity
import com.miapp.xanostorekotlin.ui.fragments.AddProductFragment
import com.miapp.xanostorekotlin.ui.fragments.ProductsFragment
import com.miapp.xanostorekotlin.ui.fragments.ProfileFragment
import com.miapp.xanostorekotlin.ui.fragments.UsersFragment

class HomeAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        setupDrawer()
        setupNavigation()
        handleBackButton()

        if (savedInstanceState == null) {
            replaceFragment(ProfileFragment(), "Perfil")
            binding.navigationViewAdmin.setCheckedItem(R.id.drawer_profile)
        }
    }

    private fun setupDrawer() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayoutAdmin,
            binding.topAppBarAdmin,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayoutAdmin.addDrawerListener(toggle)
        toggle.syncState()

        binding.topAppBarAdmin.setNavigationOnClickListener {
            binding.drawerLayoutAdmin.openDrawer(GravityCompat.START)
        }
    }

    private fun setupNavigation() {
        binding.navigationViewAdmin.setNavigationItemSelectedListener { item ->
            // Usamos nuestra nueva función centralizada para la navegación del sidebar
            navigateTo(item.itemId)
            binding.drawerLayoutAdmin.closeDrawer(GravityCompat.START)
            true
        }

        binding.bottomNavAdmin.setOnItemSelectedListener { item ->
            // También usamos la función centralizada para la barra inferior
            navigateTo(item.itemId)
            true
        }
    }

    /**
     * Permite a los fragmentos hijos o a los menús solicitar un cambio de navegación.
     * Centraliza la lógica de reemplazo de fragmentos y actualización de menús.
     * @param destinationId El ID del menú del destino (ej. R.id.drawer_products).
     */
    fun navigateTo(destinationId: Int) {
        when (destinationId) {
            R.id.drawer_products, R.id.nav_products -> {
                replaceFragment(ProductsFragment(), "Productos")
                binding.navigationViewAdmin.setCheckedItem(R.id.drawer_products)
                binding.bottomNavAdmin.selectedItemId = R.id.nav_products
            }
            R.id.drawer_users, R.id.nav_users -> {
                replaceFragment(UsersFragment(), "Usuarios")
                binding.navigationViewAdmin.setCheckedItem(R.id.drawer_users)
                binding.bottomNavAdmin.selectedItemId = R.id.nav_users
            }
            R.id.drawer_profile -> {
                replaceFragment(ProfileFragment(), "Perfil")
                binding.navigationViewAdmin.setCheckedItem(R.id.drawer_profile)
            }
            R.id.drawer_logout -> {
                showLogoutDialog()
            }
            // Agregado para el fragmento de añadir producto
            R.id.nav_add -> {
                replaceFragment(AddProductFragment(), "Añadir Producto")
                binding.navigationViewAdmin.setCheckedItem(0) // Ningún item seleccionado en el drawer
            }
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar Salida")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Sí, Salir") { _, _ ->
                tokenManager.clear()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        // Evita recargar el mismo fragmento si ya está visible
        if (supportFragmentManager.findFragmentById(binding.fragmentContainerAdmin.id)?.javaClass == fragment.javaClass) {
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerAdmin.id, fragment)
            .commit()
        binding.topAppBarAdmin.title = title
    }

    private fun handleBackButton() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayoutAdmin.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayoutAdmin.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}
