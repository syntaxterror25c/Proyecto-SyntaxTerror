package com.example.aplicacion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    // LiveData que el Fragment observará para saber si el botón debe estar activo
    private val _isLoginValid = MutableLiveData<Boolean>()
    val isLoginValid: LiveData<Boolean> get() = _isLoginValid

    // User/Pass
    fun isValidLogin(user: String, pass: String): Boolean {
        return user == "admin" && pass == "1234"
    }

    // Actualiza el LiveData cada vez que cambian los inputs
    fun updateValidation(user: String, pass: String) {
        val isValid = user.isNotEmpty() && pass.length >= 4
        // Actualizar el valor --> notificación a los observers del Fragment
        _isLoginValid.value = isValid
    }
}