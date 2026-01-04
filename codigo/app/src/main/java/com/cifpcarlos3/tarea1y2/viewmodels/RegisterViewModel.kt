package com.cifpcarlos3.tarea1y2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel que uso para la pantalla de registro.
// Aquí centralizo toda la lógica de validación de los campos del formulario.
// La idea es que el Fragment solo se encargue de la parte visual y delegue
// en este ViewModel la parte "inteligente".
class RegisterViewModel : ViewModel() {

    // LiveData que me dice si el botón "Crear cuenta" debe estar habilitado.
    // Empieza en false porque cuando entramos al registro todos los campos
    // están vacíos y todavía no quiero que el usuario pueda pulsar el botón.
    private val _isRegisterEnabled = MutableLiveData(false)
    val isRegisterEnabled: LiveData<Boolean> get() = _isRegisterEnabled

    // Este método lo llamo desde el Fragment cada vez que cambia
    // cualquiera de los campos (usuario, email, contraseñas o fecha).
    //
    // Aquí no hago validaciones súper complejas, pero sí compruebo:
    //  - que todos los campos tienen algo escrito
    //  - que las dos contraseñas coinciden
    //
    // Si se cumplen ambas cosas, activo el botón; si no, lo desactivo.
    fun onFieldsChanged(
        user: String,
        email: String,
        pass: String,
        passConfirm: String,
        birth: String
    ) {
        // Compruebo si todos los campos tienen texto (no están en blanco).
        val camposRellenos =
            user.isNotBlank() &&
                    email.isNotBlank() &&
                    pass.isNotBlank() &&
                    passConfirm.isNotBlank() &&
                    birth.isNotBlank()

        // Compruebo si las contraseñas coinciden.
        val contrasenasIguales = pass == passConfirm

        // Solo habilito el botón si TODO está relleno y las contraseñas coinciden.
        _isRegisterEnabled.value = camposRellenos && contrasenasIguales
    }

    // Función sencilla que uso desde el Fragment para saber si las contraseñas coinciden.
    // MUY IMPORTANTE: aquí NO hay ningún TODO(), solo devuelvo true/false.
    fun passwordsMatch(pass: String, confirm: String): Boolean {
        return pass == confirm
    }
}
