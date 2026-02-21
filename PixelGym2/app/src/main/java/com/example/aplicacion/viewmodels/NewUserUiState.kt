package com.example.aplicacion.viewmodels

sealed class NewUserUiState {
    object Idle : NewUserUiState()
    object Loading : NewUserUiState()
    object Created : NewUserUiState()
    data class Error(val messageRes: Int) : NewUserUiState()
}