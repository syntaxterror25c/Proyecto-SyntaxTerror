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
import com.example.aplicacion.viewmodels.ListViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var listViewModel: ListViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        // Cambio de tema: Splash --> Por defecto
        setTheme(R.style.Theme_Aplicacion)
        super.onCreate(savedInstanceState)

        // Inicialización del Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listViewModel = ViewModelProvider(this)[ListViewModel::class.java]

        /* TOOLBAR (como AcctionBar) */
        this.setSupportActionBar(binding.toolbar)

        /* DRAWER MENU */
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar, // Vincula la barra con el drawer
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        // Pintar la hamburguesa + Sincronizar el menú
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Navegación
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Drawer
        binding.navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawer_list -> {
                    navController.navigate(R.id.tabListDiscosFragment)
                    true
                }

                R.id.drawer_contact -> {
                    navController.navigate(R.id.contactFragment)
                    true
                }

                R.id.drawer_preferences -> {
                    navController.navigate(R.id.preferencesFragment)
                    true
                }

                R.id.drawer_logOut -> {
                    navController.navigate(R.id.loginFragment)
                    true
                }

                else -> false
                // Cerrar siempre
            }.also {
                binding.drawerLayout.closeDrawers()
            }
        }

        // Toolbar. Menú superior
        this.addMenuProvider(object : MenuProvider {
            // Inflar menú
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar, menu)

                // Ocultar componentes según fragment
                // Buscamos el destino actual
                val currentDest = navController.currentDestination?.id
                // Comprobar si estamos en algún listFragment para mostrar search y sort
                val isListFragment = (currentDest == R.id.listDiscosFragment)
                        || (currentDest == R.id.listDiscosFavFragment)
                        || (currentDest == R.id.tabListDiscosFragment)
                val searchItem = menu.findItem(R.id.action_search)
                searchItem?.isVisible = isListFragment
                val sortItem = menu.findItem(R.id.action_sort)
                sortItem?.isVisible = isListFragment

                // Buscador
                if (isListFragment && searchItem != null) {
                    val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
                    searchView.queryHint = R.string.hintBuscar.toString()

                    searchView.setOnQueryTextListener(object :
                        androidx.appcompat.widget.SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean = false
                        override fun onQueryTextChange(newText: String?): Boolean {
                            // Conectamos con el ViewModel
                            listViewModel.setFilter(newText)
                            return true
                        }
                    })
                }

            }

            // Listener para "ordenar" y "más" del menú superior
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sort -> {
                        listViewModel.toggleSort()
                        true
                    }
                    R.id.action_more -> {
                        true
                    }

                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)


        /* Menú inferior BNM */
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bnm_list -> {
                    navController.navigate(R.id.tabListDiscosFragment)
                    true
                }
                R.id.bnm_contact -> {
                    navController.navigate(R.id.contactFragment)
                    true
                }
                R.id.bnm_settings -> {
                    navController.navigate(R.id.preferencesFragment)
                    true
                }
                else -> false
            }
        }

        // Escuchar destinos
        navController.addOnDestinationChangedListener { _, destination, _ ->
            invalidateMenu() // Redibujar toolbar ejecutando onCreateMenu

            val isListFragment = (destination.id == R.id.listDiscosFragment)
                    || (destination.id == R.id.listDiscosFavFragment)
                    || (destination.id == R.id.tabListDiscosFragment)

            // Gestión menús completos
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    // Escondemos menus en Login y Registro
                    binding.appBarLayout.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.GONE
                    // Bloquear Drawer para que no se pueda abrir deslizando
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    binding.floatingActionButton.hide()
                }

                else -> {
                    // Mostramos los menús en el resto de la app
                    binding.appBarLayout.visibility = View.VISIBLE
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    if (isListFragment) binding.floatingActionButton.show()
                    else binding.floatingActionButton.hide()
                }
            }
        }

        // Callback personalizado
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                // Si el menú lateral (Drawer) está abierto, lo cerramos
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    // Ver dónde estamos ahora mismo
                    val currentDestination = navController.currentDestination?.id

                    when (currentDestination) {
                        // Punto de entrada tras login. No se permite retroceder. Aviso usar logout
                        R.id.tabListDiscosFragment -> Toast.makeText(
                            applicationContext,
                            R.string.cierraSesion,
                            Toast.LENGTH_SHORT
                        ).show()
                        // Volver atrás en login cierra la App. Register oK porque sólo se llega desde login
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

}