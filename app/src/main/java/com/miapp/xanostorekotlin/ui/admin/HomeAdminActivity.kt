package com.miapp.xanostorekotlin.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.api.auth.TokenManager
import com.miapp.xanostorekotlin.databinding.ActivityHomeAdminBinding
import com.miapp.xanostorekotlin.ui.auth.MainActivity

class HomeAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var tokenManager: TokenManager
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        // 1. Setup NavController
        // The ID 'fragment_container_admin' must match the ID of your NavHostFragment in the XML layout.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_admin) as NavHostFragment
        navController = navHostFragment.navController

        // 2. Setup Action Bar (TopAppBar)
        setSupportActionBar(binding.topAppBarAdmin)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_products, R.id.nav_users), // Top-level destinations
            binding.drawerLayoutAdmin
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 3. Setup DrawerLayout and Navigation
        setupDrawer()
        setupNavigation()
        handleBackButton()
    }

    /**
     * Configura el ActionBarDrawerToggle para manejar la apertura y cierre del menú lateral.
     */
    private fun setupDrawer() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayoutAdmin,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayoutAdmin.addDrawerListener(toggle)
        toggle.syncState()
    }

    /**
     * Configura los listeners para la navegación lateral (NavigationView) y la inferior (BottomNavigationView)
     * para que trabajen con el NavController.
     */
    private fun setupNavigation() {
        // Conecta el BottomNavigationView con el NavController
        binding.bottomNavAdmin.setupWithNavController(navController)

        // Listener para el menú lateral (drawer)
        binding.navigationViewAdmin.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // Navega usando el NavController
                R.id.drawer_profile -> navController.navigate(R.id.nav_profile)
                R.id.drawer_settings -> {
                    // TODO: Implementar navegación a SettingsFragment
                    // navController.navigate(R.id.nav_settings)
                }
                R.id.drawer_logout -> showLogoutDialog()
            }
            binding.drawerLayoutAdmin.closeDrawer(GravityCompat.START)
            true
        }
    }

    /**
     * Esta función ahora puede ser llamada desde cualquier fragmento alojado en esta actividad.
     * @param destinationId El ID del destino en tu grafo de navegación (e.g., R.id.nav_products).
     */
    fun navigateTo(destinationId: Int) {
        navController.navigate(destinationId)
    }

    /**
     * Muestra un diálogo de confirmación para cerrar la sesión.
     */
    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar Cierre de Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Sí, Salir") { _, _ ->
                tokenManager.clear()
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            .show()
    }

    /**
     * Controla el comportamiento del botón "Atrás" del sistema.
     */
    private fun handleBackButton() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayoutAdmin.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayoutAdmin.closeDrawer(GravityCompat.START)
                } else {
                    // Dejar que el NavController maneje el botón "Atrás"
                    if (!navController.navigateUp()) {
                        // Si no hay más en la pila de atrás, cierra la app (o haz la acción por defecto)
                        if (isEnabled) {
                            isEnabled = false
                            onBackPressedDispatcher.onBackPressed()
                            isEnabled = true
                        }
                    }
                }
            }
        })
    }

    // Permite que el ActionBarDrawerToggle maneje los eventos de clic en el ícono de menú (hamburguesa).
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Esencial para que el botón "Up" (flecha atrás) en el AppBar funcione con el NavController.
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
