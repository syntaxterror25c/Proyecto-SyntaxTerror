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
import com.example.aplicacion.viewmodels.RecursosViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var recursosViewModel: RecursosViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        // Cambio de tema: Splash --> Por defecto
        setTheme(R.style.Theme_Aplicacion)
        super.onCreate(savedInstanceState)

        // Inicialización del Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos el ViewModel compartido a nivel de Actividad
        recursosViewModel = ViewModelProvider(this)[RecursosViewModel::class.java]

        /* TOOLBAR PERSONALIZADA */
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        /* DRAWER (Menú Lateral) */
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.isDrawerIndicatorEnabled = false
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configuración del NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Listeners del Menú Lateral (Drawer)
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawer_list -> navController.navigate(R.id.tabListRecursosFragment)
                R.id.drawer_contact -> navController.navigate(R.id.contactFragment)
                R.id.drawer_preferences -> navController.navigate(R.id.preferencesFragment)
                R.id.drawer_logOut -> navController.navigate(R.id.loginFragment)
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        // Proveedor de Menú para la Toolbar (Búsqueda y Ordenación)
        this.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar, menu)

                val currentDest = navController.currentDestination?.id
                val isListFragment = (currentDest == R.id.listRecursosFragment)
                        || (currentDest == R.id.listRecursosFavFragment)
                        || (currentDest == R.id.tabListRecursosFragment)

                supportActionBar?.setDisplayHomeAsUpEnabled(!isListFragment)

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

        /* Bottom Navigation (Menú Inferior) */
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bnm_list -> navController.navigate(R.id.tabListRecursosFragment)
                R.id.bnm_contact -> navController.navigate(R.id.contactFragment)
                R.id.bnm_settings -> navController.navigate(R.id.preferencesFragment)
            }
            true
        }

        // Observador de cambios de destino para ocultar/mostrar elementos de UI
        navController.addOnDestinationChangedListener { _, destination, _ ->
            invalidateMenu()
            val isListFragment = (destination.id == R.id.listRecursosFragment)
                    || (destination.id == R.id.tabListRecursosFragment)

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
                    if (isListFragment) binding.floatingActionButton.show() else binding.floatingActionButton.hide()
                }
            }
        }

        // Gestión personalizada del botón Atrás
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
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