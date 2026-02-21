package com.example.aplicacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.firebase.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.aplicacion.R
class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // StateFlow para el estado de la UI
    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val uiState: StateFlow<UserUiState> = _uiState

    // Función de Login con Firebase
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            val result = authRepository.login(email, pass)

            result.onSuccess { user ->
                _uiState.value = UserUiState.Authenticated(user)
            }.onFailure { exception ->
                val errorRes = when (exception.message) {
                    "user_not_found" -> R.string.error_user_not_found
                    else -> R.string.error_unknown
                }
                _uiState.value = UserUiState.Error(errorRes)
            }
        }
    }

    // Habilitar botón con StateFlow
    private val _isLoginValid = MutableStateFlow(false)
    val isLoginValid: StateFlow<Boolean> = _isLoginValid

    fun updateValidation(user: String, pass: String) {
        _isLoginValid.value = user.isNotEmpty() && pass.length >= 4
    }
}