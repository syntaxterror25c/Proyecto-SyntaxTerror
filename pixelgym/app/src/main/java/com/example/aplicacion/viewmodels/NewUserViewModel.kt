package com.example.aplicacion.viewmodels

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewUserViewModel : ViewModel() {

    // 🔒 Interno (privado)
    private val _isRegisterValid = MutableLiveData(false)

    // 🌍 Público
    val isRegisterValid: LiveData<Boolean> = _isRegisterValid

    fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isPasswordValid(password: String): Boolean =
        password.length >= 6

    fun passwordsMatch(p1: String, p2: String): Boolean =
        p1 == p2

    fun isNameValid(name: String): Boolean =
        name.isNotBlank()

    fun isPhoneValid(phone: String): Boolean =
        phone.length == 9 && phone.all { it.isDigit() }

    fun isTarifaValid(tarifa: String): Boolean =
        tarifa.isNotBlank()

    fun updateValidation(
        email: String,
        pass1: String,
        pass2: String,
        name: String,
        phone: String,
        tarifa: String,
        photoSelected: Boolean
    ) {
        _isRegisterValid.value =
            isEmailValid(email) &&
                    isPasswordValid(pass1) &&
                    isPasswordValid(pass2) &&
                    passwordsMatch(pass1, pass2) &&
                    isNameValid(name) &&
                    isPhoneValid(phone) &&
                    isTarifaValid(tarifa)
        // Foto opcional: NO usamos photoSelected
    }
}
