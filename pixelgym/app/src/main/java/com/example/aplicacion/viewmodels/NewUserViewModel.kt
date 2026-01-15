package com.example.aplicacion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewUserViewModel : ViewModel() {

    // LiveData que el Fragment observará para el estado de habilitación del botón
    private val _isRegisterValid = MutableLiveData<Boolean>()
    val isRegisterValid: LiveData<Boolean> get() = _isRegisterValid

    // validar contraseñas iguales
    fun passwordsMatch(pass1: String, pass2: String): Boolean {
        return pass1 == pass2
    }

    // actualiza el LiveData
    fun updateValidation(user: String, pass1: String, pass2: String, date: String) {
        // longitud de los campos
        val isValid = user.isNotEmpty() && pass1.length >= 4 && pass2.length >= 4 && date.isNotEmpty()
        // notificar al fragment si la validación ha cambiado
        _isRegisterValid.value = isValid
    }
}