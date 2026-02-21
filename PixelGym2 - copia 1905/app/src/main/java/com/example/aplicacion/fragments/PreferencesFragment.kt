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

        // --- CARGA DE DATOS CON VALORES POR DEFECTO ACTIVADOS (true) ---
        val isEnglish = prefs.getBoolean("is_english", true)
        val isDarkModeActive = prefs.getBoolean("dark_mode", true)
        val isNotificationsActive = prefs.getBoolean("notifications", true)

        // --- CONFIGURAR VISTA ---
        binding.switchSync.isChecked = isEnglish
        binding.switchSync.text = if (isEnglish) "Language: English" else "Idioma: Español"

        binding.switchDarkMode.isChecked = isDarkModeActive
        binding.switchNotifications.isChecked = isNotificationsActive

        // --- LISTENERS ---

        // Modo Oscuro
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("dark_mode", isChecked).apply()
            val modoDestino = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(modoDestino)
        }

        // Notificaciones (Corregida la clave)
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("notifications", isChecked).apply()
        }

        // Idioma
        binding.switchSync.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("is_english", isChecked).apply()
            val langCode = if (isChecked) "en" else "es"

            // Actualizar texto del switch al momento
            binding.switchSync.text = if (isChecked) "Language: English" else "Idioma: Español"

            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }




}