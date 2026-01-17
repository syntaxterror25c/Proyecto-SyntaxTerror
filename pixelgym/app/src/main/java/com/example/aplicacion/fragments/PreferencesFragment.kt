package com.example.aplicacion.fragments
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentPreferencesBinding

class PreferencesFragment : Fragment(R.layout.fragment_preferences) {

    private lateinit var binding: FragmentPreferencesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPreferencesBinding.bind(view)

        // Obtener las preferencias guardadas
        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Calcular estado modo oscuro SO
        val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isSystemInDarkMode = currentMode == Configuration.UI_MODE_NIGHT_YES

        // Si no existe la clave "dark_mode", usamos el estado actual del sistema (isSystemInDarkMode)
        val isDarkModeActive = prefs.getBoolean("dark_mode", isSystemInDarkMode)

        // Cargar interruptures
        binding.switchDarkMode.isChecked = isDarkModeActive
        binding.switchNotifications.isChecked = prefs.getBoolean("notifications", false)
        binding.switchSync.isChecked = prefs.getBoolean("sync", false)

        // Listener Modo Oscuro
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("dark_mode", isChecked).apply()
            val modoDestino = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            // Solo recreamos la actividad si el modo elegido es distinto al actual
            if (AppCompatDelegate.getDefaultNightMode() != modoDestino) {
                AppCompatDelegate.setDefaultNightMode(modoDestino)
            }
        }
        // Listener Notificaciones
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("notifications", isChecked).apply()
        }
        // Listener Sincronización
        binding.switchSync.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("sync", isChecked).apply()
        }

    }
}