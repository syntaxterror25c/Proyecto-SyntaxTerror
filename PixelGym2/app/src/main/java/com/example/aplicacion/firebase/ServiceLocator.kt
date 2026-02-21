package com.example.aplicacion.firebase

import com.example.aplicacion.firebase.data.AuthDataSource
import com.example.aplicacion.firebase.data.SesionDataSource // Cambio aquí
import com.example.aplicacion.firebase.data.UserDataSource

object ServiceLocator {
    // Instancias base de Firebase
    private val auth = FirebaseProvider.provideAuth()
    private val firestore = FirebaseProvider.provideFirestore()

    // DataSources que tocan Firebase
    val authDataSource by lazy { AuthDataSource(auth) }
    val userDataSource by lazy { UserDataSource(firestore) }

    // Sustituimos discoDataSource por sesionDataSource
    val sesionDataSource by lazy { SesionDataSource(firestore) }

    // Repositories que organizan los datos para el ViewModel
    val authRepository by lazy {
        AuthRepository(authDataSource, userDataSource) // Ahora le pasamos ambos
    }

    // El nuevo repositorio del Gimnasio
    val gymRepository by lazy { GymRepository(sesionDataSource) }


}