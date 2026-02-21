package com.example.aplicacion

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.aplicacion.databinding.ActivityMainBinding
import com.example.aplicacion.firebase.ServiceLocator
import com.example.aplicacion.viewmodels.GymViewModel
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.aplicacion.viewmodels.GymViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var gymViewModel: GymViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Aplicacion)

        // --- PREFERENCIAS DE MODO OSCURO ---
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", true)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- VIEWMODEL ---
        gymViewModel = ViewModelProvider(
            this,
            GymViewModelFactory(ServiceLocator.gymRepository, ServiceLocator.authRepository)
        )[GymViewModel::class.java]

        // PRUEBA MUCHO OJO
        gymViewModel.resetTotalGimnasioPruebas()


        this.setSupportActionBar(binding.toolbar)

        // --- NAVIGATION DRAWER ---
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Listener del Drawer (Menú lateral)
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawer_list -> navController.navigate(R.id.tabListDiscosFragment) // Mantenemos el ID del navGraph por ahora
                R.id.drawer_contact -> navController.navigate(R.id.contactFragment)
                R.id.drawer_preferences -> navController.navigate(R.id.preferencesFragment)
                R.id.drawer_logOut -> {
                    ServiceLocator.authRepository.logout()
                    navController.navigate(R.id.loginFragment)
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        // --- GESTIÓN MENÚ SUPERIOR (TOOLBAR) ---
        this.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar, menu)

                // Ocultamos buscador y sort por ahora para evitar errores
                menu.findItem(R.id.action_search)?.isVisible = false
                menu.findItem(R.id.action_sort)?.isVisible = false
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false // Dejamos que onOptionsItemSelected maneje el resto
            }
        }, this, Lifecycle.State.STARTED)

        // --- BOTTOM NAVIGATION ---
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bnm_list -> navController.navigate(R.id.tabListDiscosFragment)
                R.id.bnm_contact -> navController.navigate(R.id.contactFragment)
                R.id.bnm_settings -> navController.navigate(R.id.preferencesFragment)
            }
            true
        }

        // --- CONTROL DE VISIBILIDAD DE INTERFAZ SEGÚN DESTINO ---
        navController.addOnDestinationChangedListener { _, destination, _ ->
            actualizarHeaderNavigation()

            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    binding.appBarLayout.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.GONE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    binding.floatingActionButton.hide()
                }
                else -> {
                    binding.appBarLayout.visibility = View.VISIBLE
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    // El FAB lo ocultamos de momento porque servía para añadir discos
                    binding.floatingActionButton.hide()
                }
            }
        }

        // --- BOTÓN ATRÁS ---
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    when (navController.currentDestination?.id) {
                        R.id.tabListDiscosFragment -> Toast.makeText(applicationContext, "Cierra sesión para salir", Toast.LENGTH_SHORT).show()
                        R.id.loginFragment -> finish()
                        else -> {
                            isEnabled = false
                            onBackPressedDispatcher.onBackPressed()
                            isEnabled = true
                        }
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun actualizarHeaderNavigation() {
        val user = ServiceLocator.authRepository.getCurrentUser()
        if (binding.navigationView.headerCount > 0) {
            val headerView = binding.navigationView.getHeaderView(0)
            val tvEmail = headerView.findViewById<android.widget.TextView>(R.id.textViewName)
            tvEmail?.text = user?.email ?: "Usuario Anónimo"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                // Navegar al login
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navHostFragment.navController.navigate(R.id.loginFragment)
                true
            }
            R.id.menu_about -> {
                AlertDialog.Builder(this)
                    .setTitle("PixelGym")
                    .setMessage("(c) SyntaxTerror - 2026\nVersión 1.0 - Gestión de Sesiones")
                    .setPositiveButton("OK", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}