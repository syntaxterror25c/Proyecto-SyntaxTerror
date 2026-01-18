package com.example.aplicacion

import android.content.Context // Importante añadir
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate // Importante añadir
import androidx.core.os.LocaleListCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.aplicacion.databinding.ActivityMainBinding
import com.example.aplicacion.viewmodels.RecursosViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var recursosViewModel: RecursosViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // APLICAR IDIOMA (Añade esto aquí)
        val isEnglish = prefs.getBoolean("language_en", false)
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(if (isEnglish) "en" else "es")
        AppCompatDelegate.setApplicationLocales(appLocale)

        // APLICAR MODO OSCURO ANTES DE MOSTRAR LA VISTA
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setTheme(R.style.Theme_Aplicacion)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recursosViewModel = ViewModelProvider(this)[RecursosViewModel::class.java]

        /* TOOLBAR */
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        /* DRAWER */
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.isDrawerIndicatorEnabled = false
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Listener del Drawer
        binding.navigationView.setNavigationItemSelectedListener { item ->
            val currentDest = navController.currentDestination?.id

            when (item.itemId) {
                R.id.drawer_list -> if (currentDest != R.id.tabListRecursosFragment) navController.navigate(R.id.tabListRecursosFragment)
                R.id.drawer_contact -> if (currentDest != R.id.contactFragment) navController.navigate(R.id.contactFragment)
                R.id.drawer_preferences -> if (currentDest != R.id.preferencesFragment) navController.navigate(R.id.preferencesFragment)
                R.id.drawer_logOut -> navController.navigate(R.id.loginFragment)
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        /* TOOLBAR MENU PROVIDER */
        this.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar, menu)

                val currentDest = navController.currentDestination?.id
                val isListFragment = (currentDest == R.id.tabListRecursosFragment)

                supportActionBar?.setDisplayHomeAsUpEnabled(!isListFragment)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

                val searchItem = menu.findItem(R.id.action_search)
                searchItem?.isVisible = isListFragment
                val sortItem = menu.findItem(R.id.action_sort)
                sortItem?.isVisible = isListFragment

                if (isListFragment && searchItem != null) {
                    val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
                    searchView.queryHint = getString(R.string.hintBuscar)
                    searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean = false
                        override fun onQueryTextChange(newText: String?): Boolean {
                            recursosViewModel.setFilter(newText)
                            return true
                        }
                    })
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        onBackPressedDispatcher.onBackPressed()
                        true
                    }
                    R.id.action_open_drawer -> {
                        binding.drawerLayout.openDrawer(GravityCompat.END)
                        true
                    }
                    R.id.action_sort -> {
                        recursosViewModel.toggleSort()
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)

        /* BOTTOM NAVIGATION */
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val currentDest = navController.currentDestination?.id
            if (currentDest == item.itemId) return@setOnItemSelectedListener false

            when (item.itemId) {
                R.id.tabListRecursosFragment -> navController.navigate(R.id.tabListRecursosFragment)
                R.id.contactFragment -> navController.navigate(R.id.contactFragment) // Nuevo destino
                R.id.misReservasFragment -> navController.navigate(R.id.misReservasFragment)
            }
            true
        }

        /* DESTINATION CHANGES */
        navController.addOnDestinationChangedListener { _, destination, _ ->
            invalidateMenu()

            // dentificamos si estamos en la lista principal
            val isListFragment = (destination.id == R.id.tabListRecursosFragment)

            // SINCRONIZACIÓN DEL BOTTOM NAV:
            // Si navegamos desde el Drawer a "Contacto",el Bottom Nav debe marcar el icono de Contacto
            binding.bottomNavigation.menu.findItem(destination.id)?.isChecked = true

            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    // Pantallas de acceso: ocultamos todo para que sea pantalla completa
                    binding.appBarLayout.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.GONE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    binding.floatingActionButton.hide()
                }
                else -> {
                    // Pantallas principales: mostramos navegación y desbloqueamos el Drawer
                    binding.appBarLayout.visibility = View.VISIBLE
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                    // El botón flotante (FAB) solo aparece en la lista
                    if (isListFragment) {
                        binding.floatingActionButton.show()
                    } else {
                        binding.floatingActionButton.hide()
                    }
                }
            }
        }

        /* BACK BUTTON MANAGEMENT */
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    val currentDestination = navController.currentDestination?.id
                    if (currentDestination == R.id.tabListRecursosFragment) {
                        Toast.makeText(applicationContext, R.string.cierraSesion, Toast.LENGTH_SHORT).show()
                    } else if (currentDestination == R.id.loginFragment) {
                        finish()
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        })
    }
}