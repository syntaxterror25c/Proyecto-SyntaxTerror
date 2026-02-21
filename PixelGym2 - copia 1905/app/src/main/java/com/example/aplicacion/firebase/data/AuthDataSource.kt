package com.example.aplicacion.firebase.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthDataSource(private val firebaseAuth: FirebaseAuth) {

    // Autenticación con email y contraseña
    suspend fun login(email: String, pass: String): FirebaseUser? {
        val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
        return result.user
    }

    // Registro
    suspend fun signup(email: String, pass: String): FirebaseUser? {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
        return result.user
    }

    // Cerrar sesión
    fun logout() {
        firebaseAuth.signOut()
    }

    // Usuario actual (devuelve un objeto de tipo FirebaseUser)
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}