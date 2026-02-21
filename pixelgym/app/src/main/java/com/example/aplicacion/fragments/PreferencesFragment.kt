package com.example.aplicacion.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentPreferencesBinding

class PreferencesFragment : Fragment(R.layout.fragment_preferences) {

    private lateinit var binding: FragmentPreferencesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPreferencesBinding.bind(view)

        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // --- 1. ESTADO INICIAL ---

        // Modo Oscuro: Detectar si está activo
        val isDarkModeActuallyActive = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        binding.switchDarkMode.isChecked = isDarkModeActuallyActive

        // Idioma: Detectar si el idioma actual es Inglés
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        binding.switchLanguage.isChecked = currentLocales.toLanguageTags() == "en"

        // Notificaciones
        binding.switchNotifications.isChecked = prefs.getBoolean("notifications", false)

        // --- 2. LISTENERS ---

        // Cambiar IDIOMA (Forma Moderna sin errores)
        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("language_en", isChecked).apply()

            val langCode = if (isChecked) "en" else "es"
            // Esta es la línea mágica que hace todo el trabajo por ti:
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

        // Cambiar MODO OSCURO
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val modoDestino = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            if (AppCompatDelegate.getDefaultNightMode() != modoDestino) {
                editor.putBoolean("dark_mode", isChecked).apply()
                AppCompatDelegate.setDefaultNightMode(modoDestino)
            }
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("notifications", isChecked).apply()
        }
    }
}