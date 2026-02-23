package com.example.aplicacion.firebase

import com.example.aplicacion.firebase.data.AuthDataSource
import com.example.aplicacion.firebase.data.SesionDataSource
import com.example.aplicacion.firebase.data.UserDataSource
// El ServiceLocator se encarga de crear y proporcionar las instancias de los DataSources y Repositories
object ServiceLocator {
    private val auth = FirebaseProvider.provideAuth()
    private val firestore = FirebaseProvider.provideFirestore()

    val authDataSource by lazy { AuthDataSource(auth) }
    val userDataSource by lazy { UserDataSource(firestore) }

    // Limpio y sin parámetros
    val sesionDataSource by lazy { SesionDataSource() }

    val authRepository by lazy {
        AuthRepository(authDataSource, userDataSource)
    }

    // Usando el repositorio con el nombre correcto
    val gymRepository by lazy { GymRepository(sesionDataSource) }
}