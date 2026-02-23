package com.example.aplicacion.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// Proveedor de instancias de FirebaseAuth y FirebaseFirestore utilizando el patrón Singleton todo empieza aqui
object FirebaseProvider {
    @Volatile private var auth: FirebaseAuth? = null
    @Volatile private var firestore: FirebaseFirestore? = null

    fun provideAuth(): FirebaseAuth =
        auth ?: synchronized(this) {
            auth ?: FirebaseAuth.getInstance().also { auth = it }
        }

    fun provideFirestore(): FirebaseFirestore =
        firestore ?: synchronized(this) {
            firestore ?: FirebaseFirestore.getInstance().also { firestore = it }
        }
}