package com.example.aplicacion.firebase

import com.example.aplicacion.firebase.data.AuthDataSource
import com.example.aplicacion.firebase.data.UserDataSource
import com.example.aplicacion.models.Tarifa
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) {

    // 1. Obtener el usuario que tiene la sesión iniciada
    fun getCurrentUser(): FirebaseUser? {
        return authDataSource.getCurrentUser()
    }

    // 2. Proceso de Login
    suspend fun login(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val user = authDataSource.login(email, pass)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("user_not_found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. Proceso de Logout
    fun logout() {
        authDataSource.logout()
    }

    /**
     * 4. Registro completo (Atómico):
     * Crea la cuenta en Auth y el perfil en Firestore con el Plan "tatuado".
     */
    suspend fun signupCompleto(
        email: String,
        pass: String,
        nombre: String,
        telefono: String,
        idPlan: String,
        nombrePlan: String,
        limiteSesiones: Int,
        fechaFin: String
    ): Result<FirebaseUser> {
        return try {
            // Paso A: Crear usuario en FirebaseAuth
            val user = authDataSource.signup(email, pass)

            if (user != null) {
                // Paso B: Si se creó bien, guardamos sus datos en la colección "usuarios" de Firestore
                userDataSource.saveUserInFirestore(
                    uid = user.uid,
                    nombreUsuario = nombre,
                    email = email,
                    telefono = telefono,
                    idPlan = idPlan,
                    nombrePlan = nombrePlan,
                    limiteSesiones = limiteSesiones,
                    fechaFin = fechaFin
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Error al crear la cuenta de autenticación"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun fetchTarifas(): List<Tarifa> {
        return try {
            // Necesitamos obtener la instancia de Firestore primero
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val snapshot = db.collection("tarifas").get().await()
            snapshot.toObjects(Tarifa::class.java)
        } catch (e: Exception) {
            android.util.Log.e("ERROR_REPO", "Error cargando tarifas: ${e.message}")
            emptyList()
        }
    }
}