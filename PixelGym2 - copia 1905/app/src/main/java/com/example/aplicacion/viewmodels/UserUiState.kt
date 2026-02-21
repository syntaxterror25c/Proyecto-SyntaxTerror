package com.example.aplicacion.viewmodels

import com.google.firebase.auth.FirebaseUser
sealed interface UserUiState {
    object Idle : UserUiState
    object Loading : UserUiState
    data class Authenticated(val user: FirebaseUser) : UserUiState
    data class Error(val messageRes: Int) : UserUiState
}