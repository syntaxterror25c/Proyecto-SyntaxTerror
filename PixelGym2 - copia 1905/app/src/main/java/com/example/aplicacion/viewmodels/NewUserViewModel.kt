package com.example.aplicacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.R
import com.example.aplicacion.firebase.AuthRepository
import com.example.aplicacion.models.Tarifa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewUserViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<NewUserUiState>(NewUserUiState.Idle)
    val uiState: StateFlow<NewUserUiState> = _uiState

    private val _isRegisterValid = MutableStateFlow(false)
    val isRegisterValid: StateFlow<Boolean> = _isRegisterValid

    private val _tarifas = MutableStateFlow<List<Tarifa>>(emptyList())
    val tarifas: StateFlow<List<Tarifa>> = _tarifas

    // Creamos una función para cargar los planes
    fun cargarPlanes() {
        viewModelScope.launch {
            val lista = authRepository.fetchTarifas()
            // Usamos sortedBy para que devuelva la lista ya ordenada
            _tarifas.value = lista.sortedBy { it.precio }
        }
    }
    fun updateValidation(user: String, pass: String, confirm: String, nombre: String) {
        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(user).matches()
        val passValid = pass.length >= 6
        val match = pass == confirm && confirm.isNotEmpty()
        val nombreValid = nombre.isNotEmpty()

        _isRegisterValid.value = emailValid && passValid && match && nombreValid
    }

    fun register(email: String, pass: String, nombre: String, tarifa: Tarifa) {
        viewModelScope.launch {
            _uiState.value = NewUserUiState.Loading

            val result = authRepository.signupCompleto(
                email = email,
                pass = pass,
                nombre = nombre,
                idPlan = tarifa.nombre.uppercase(),
                nombrePlan = tarifa.nombre,
                limiteSesiones = tarifa.creditos,
                fechaFin = "2026-12-31" // FASE2 fechaFin según fecha de pago + 1 mes
            )

            result.onSuccess {
                _uiState.value = NewUserUiState.Created
            }.onFailure { exception ->
                _uiState.value = when (exception) {
                    is com.google.firebase.auth.FirebaseAuthUserCollisionException -> {
                        NewUserUiState.Error(R.string.error_email_ya_registrado)
                    }
                    else -> {
                        NewUserUiState.Error(R.string.error_registro_generico)
                    }
                }
            }
        }
    }
}