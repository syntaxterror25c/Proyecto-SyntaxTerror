package com.example.aplicacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aplicacion.firebase.AuthRepository
import com.example.aplicacion.firebase.GymRepository

class GymViewModelFactory(
    private val gymRepository: GymRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GymViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GymViewModel(gymRepository, authRepository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}