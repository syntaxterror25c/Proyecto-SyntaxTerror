package com.example.aplicacion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aplicacion.data.RecursosRepositorio
import com.example.aplicacion.data.Usuario

/**
 * ViewModel encargado de la lógica de autenticación.
 * Centraliza el estado del usuario logueado y la validación del formulario.
 */
class AuthViewModel : ViewModel() {

    // Instanciamos el repositorio para acceder a la "base de datos" de usuarios
    private val repositorio = RecursosRepositorio()

    // LiveData para habilitar/deshabilitar el botón de login en la UI
    private val _isLoginValid = MutableLiveData<Boolean>()
    val isLoginValid: LiveData<Boolean> get() = _isLoginValid

    // LiveData para guardar el objeto Usuario que ha iniciado sesión
    private val _usuarioLogueado = MutableLiveData<Usuario?>()
    val usuarioLogueado: LiveData<Usuario?> get() = _usuarioLogueado

    /**
     * Intenta realizar el login delegando la búsqueda al repositorio.
     * @return true si las credenciales son válidas, false en caso contrario.
     */
    fun login(user: String, pass: String): Boolean {
        // Llamamos al método del repositorio que centraliza los usuarios de prueba
        val usuarioEncontrado = repositorio.comprobarLogin(user, pass)

        // Actualizamos el estado del LiveData (esto notificará a los observadores)
        _usuarioLogueado.value = usuarioEncontrado

        return usuarioEncontrado != null
    }

    /**
     * Limpia los datos de la sesión actual.
     */
    fun logout() {
        _usuarioLogueado.value = null
    }

    /**
     * Valida si los campos de texto cumplen con los requisitos mínimos.
     * Se llama desde los listeners de texto del LoginFragment.
     */
    fun updateValidation(user: String, pass: String) {
        // Regla: Usuario no vacío y contraseña de al menos 4 caracteres
        val isValid = user.isNotEmpty() && pass.length >= 4
        _isLoginValid.value = isValid
    }
}