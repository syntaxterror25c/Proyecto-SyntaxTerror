package com.cifpcarlos3.tarea1y2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel de la pantalla de LOGIN.
// Encargado de validar usuario/contraseña y controlar si el botón está habilitado.
class LoginViewModel : ViewModel() {

    // LiveData para saber si el botón debe estar activo
    private val _isLoginEnabled = MutableLiveData(false)
    val isLoginEnabled: LiveData<Boolean> get() = _isLoginEnabled

    // Cada vez que el usuario escribe, el fragmento llama a este método
    fun onFieldsChanged(username: String, password: String) {

        val userValid = username.isNotEmpty()      // al menos 1 letra
        val passValid = password.length >= 4       // mínimo 4 letras

        // Solo activo el botón si ambas condiciones se cumplen
        _isLoginEnabled.value = userValid && passValid
    }

    // Comprobación final de login (regla de la tarea)
    fun isValidLogin(username: String, password: String): Boolean {
        return username == "admin" && password == "1234"
    }
}