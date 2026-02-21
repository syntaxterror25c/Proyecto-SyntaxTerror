package com.example.aplicacion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Validación para habilitar botón (lo que ya usas)
    private val _isLoginValid = MutableLiveData(false)
    val isLoginValid: LiveData<Boolean> = _isLoginValid

    fun updateValidation(username: String, password: String) {
        _isLoginValid.value = username.isNotBlank() && password.length >= 6
    }

    // Estado del login (nuevo)
    sealed class LoginState {
        data object Idle : LoginState()
        data object Loading : LoginState()
        data class Success(val uid: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String) {
        val e = email.trim()
        val p = password.trim()

        _loginState.value = LoginState.Loading

        auth.signInWithEmailAndPassword(e, p)
            .addOnSuccessListener {
                val uid = auth.currentUser!!.uid

                // (Opcional pero recomendado) crear/asegurar doc /users/{uid}
                ensureUserDoc(uid, e)

                _loginState.value = LoginState.Success(uid)
            }
            .addOnFailureListener { ex ->
                _loginState.value = LoginState.Error(ex.message ?: "Error de login")
            }
    }

    private fun ensureUserDoc(uid: String, email: String) {
        val ref = db.collection("users").document(uid)
        ref.get()
            .addOnSuccessListener { snap ->
                if (!snap.exists()) {
                    val data = hashMapOf(
                        "email" to email,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    ref.set(data)
                }
            }
        // si falla aquí no bloqueamos el login
    }
}
