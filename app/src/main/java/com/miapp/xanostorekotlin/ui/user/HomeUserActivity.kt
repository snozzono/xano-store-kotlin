package com.miapp.xanostorekotlin.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.api.auth.TokenManager
import com.miapp.xanostorekotlin.databinding.ActivityHomeUserBinding
import com.miapp.xanostorekotlin.ui.auth.MainActivity

class HomeUserActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeUserBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tokenManager = TokenManager(this)

        // 1. Set the Toolbar as the support action bar
        setSupportActionBar(binding.toolbar)

        // 2. Find NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home_user) as NavHostFragment
        val navController = navHostFragment.navController

        // 3. Setup AppBarConfiguration for the drawer
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_user_products), // Top-level destinations that don't show a back arrow
            binding.drawerLayout
        )

        // 4. Connect the ActionBar with the NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 5. Connect the navigation views with the NavController
        binding.navView.setupWithNavController(navController)
        binding.navDrawerView.setupWithNavController(navController)

        // 6. Set the listener for the drawer items
        binding.navDrawerView.setNavigationItemSelectedListener(this)

        // 7. Update header with user info
        updateNavHeader()
    }

    private fun updateNavHeader() {
        val headerView = binding.navDrawerView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.nav_header_user_name)
        val userEmailTextView = headerView.findViewById<TextView>(R.id.nav_header_user_email)

        userNameTextView.text = tokenManager.getUserName()
        userEmailTextView.text = tokenManager.getUserEmail()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home_user) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_user_profile -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home_user) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.nav_user_profile)
            }
            R.id.nav_logout -> {
                tokenManager.clear()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
