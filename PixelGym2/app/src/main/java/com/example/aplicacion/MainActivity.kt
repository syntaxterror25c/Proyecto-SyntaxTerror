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

class MainActivity : AppCompatActivity() {
    private lateinit var gymViewModel: GymViewModel
    private lateinit var binding: ActivityMainBinding

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

        // ******************
        // PRUEBA MUCHO OJO
         gymViewModel.resetTotalGimnasioPruebas()
        // ******************

        this.setSupportActionBar(binding.toolbar)

        // Habilita la flecha de atrás
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // --- LISTENER NAVIGATION DRAWER (MENÚ LATERAL) ---
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawer_list -> navController.navigate(R.id.tabListReservasFragment)
                R.id.drawer_contact -> navController.navigate(R.id.contactFragment)
                R.id.drawer_preferences -> navController.navigate(R.id.preferencesFragment)
                R.id.drawer_about -> mostrarAcercaDe()
                R.id.drawer_logOut -> {
                    ServiceLocator.authRepository.logout()
                    navController.navigate(R.id.loginFragment)
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        // --- GESTIÓN MENÚ SUPERIOR (LUIPA, ORDENAR Y HAMBURGUESA) ---
        this.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar, menu)

                val currentDest = navController.currentDestination?.id
                val esPantallaDeLista = currentDest == R.id.tabListReservasFragment

                val searchItem = menu.findItem(R.id.action_search)
                val sortItem = menu.findItem(R.id.action_sort)
                val drawerItem = menu.findItem(R.id.action_open_drawer)

                // Visibilidad según fragmento
                searchItem?.isVisible = esPantallaDeLista
                sortItem?.isVisible = esPantallaDeLista
                drawerItem?.isVisible = (currentDest != R.id.loginFragment && currentDest != R.id.registerFragment)

                // Configuración de la búsqueda (Lupa)
                if (esPantallaDeLista && searchItem != null) {
                    val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
                    searchView.queryHint = "Buscar gimnasio..."
                    searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean = false
                        override fun onQueryTextChange(newText: String?): Boolean {
                            gymViewModel.setFilter(newText) // Asegúrate de tener este método en tu ViewModel
                            return true
                        }
                    })
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sort -> {
                        gymViewModel.toggleSort() // Asegúrate de tener este método en tu ViewModel
                        true
                    }
                    R.id.action_open_drawer -> {
                        binding.drawerLayout.openDrawer(GravityCompat.START)
                        true
                    }
                    android.R.id.home -> {
                        onBackPressedDispatcher.onBackPressed()
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.STARTED)

        // --- BOTTOM NAVIGATION ---
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bnm_list -> navController.navigate(R.id.tabListReservasFragment)
                R.id.bnm_contact -> navController.navigate(R.id.contactFragment)
                R.id.bnm_settings -> navController.navigate(R.id.preferencesFragment)
            }
            true
        }

        // --- CONTROL DE VISIBILIDAD DE INTERFAZ ---
        navController.addOnDestinationChangedListener { _, destination, _ ->
            actualizarHeaderNavigation()

            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    binding.appBarLayout.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.GONE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                else -> {
                    binding.appBarLayout.visibility = View.VISIBLE
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }

            // Lógica de la Flecha Atrás (Solo en pantallas secundarias)
            val pantallasConFlecha = listOf(
                R.id.contactFragment,
                R.id.detalleActividadFragment,
                R.id.preferencesFragment,
                R.id.reservaFragment
            )
            supportActionBar?.setDisplayHomeAsUpEnabled(destination.id in pantallasConFlecha)

            // Forzar refresco de los iconos de la Toolbar
            invalidateMenu()
            binding.floatingActionButton.hide()
        }

        // --- CONTROL DEL BOTÓN ATRÁS FÍSICO ---
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    when (navController.currentDestination?.id) {
                        R.id.tabListReservasFragment -> Toast.makeText(applicationContext, "Cierra sesión para salir", Toast.LENGTH_SHORT).show()
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

    private fun mostrarAcercaDe() {
        AlertDialog.Builder(this)
            .setTitle("Acerca de PixelGym")
            .setMessage("(c) SyntaxTerror - 2026\nVersión 2.0 - Material 3 Edition")
            .setPositiveButton("Cerrar", null)
            .show()
    }
}