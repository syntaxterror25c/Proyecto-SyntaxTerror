package com.example.aplicacion.viewmodels

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel de registro.
 *
 * Aquí centralizo las validaciones para que el Fragment solo "pinte" la UI.
 * Así es más fácil habilitar/deshabilitar el botón en tiempo real.
 */
class NewUserViewModel : ViewModel() {

    // LiveData que el Fragment observa para activar/desactivar el botón
    private val _isRegisterValid = MutableLiveData(false)
    val isRegisterValid: LiveData<Boolean> get() = _isRegisterValid

    // --- Validaciones típicas ---

    fun isEmailValid(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(pass: String): Boolean {
        // > 4 y < 8  =>  entre 5 y 7
        return pass.length in 5..7
    }

    fun passwordsMatch(pass1: String, pass2: String): Boolean {
        return pass1 == pass2
    }

    fun isNameValid(name: String): Boolean {
        // Solo letras (incluye acentos) y espacios
        if (name.isBlank()) return false
        return name.all { it.isLetter() || it.isWhitespace() }
    }

    fun isPhoneValid(phone: String): Boolean {
        // Solo números y exactamente 9 dígitos
        return phone.length == 9 && phone.all { it.isDigit() }
    }

    fun isTarifaValid(tarifa: String): Boolean {
        return tarifa.isNotBlank()
    }

    /**
     * Actualizo el LiveData que controla el botón.
     * Lo llamo cada vez que cambia cualquier campo.
     *
     * NOTA: la foto la dejo opcional (photoSelected no entra en la condición),
     * pero te dejo el parámetro por si quieres hacerla obligatoria.
     */
    fun updateValidation(
        email: String,
        pass1: String,
        pass2: String,
        name: String,
        phone: String,
        tarifa: String,
        photoSelected: Boolean
    ) {
        val valid =
            isEmailValid(email) &&
            isPasswordValid(pass1) &&
            isPasswordValid(pass2) &&
            passwordsMatch(pass1, pass2) &&
            isNameValid(name) &&
            isPhoneValid(phone) &&
            isTarifaValid(tarifa)
            // Si quisieras que la foto fuese obligatoria:
            // && photoSelected

        _isRegisterValid.value = valid
    }
}
