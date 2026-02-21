package com.example.aplicacion.firebase.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserDataSource(private val db: FirebaseFirestore) {

    // Ahora pasamos también el objeto del Plan seleccionado para "tatuarlo" en el usuario
    suspend fun saveUserInFirestore(
        uid: String,
        nombreUsuario: String,
        email: String,
        telefono: String,
        idPlan: String,           // id_plan (COPIA DE PLANES)
        nombrePlan: String,       // nombre_plan (COPIA DE PLANES)
        limiteSesiones: Int,      // limite_sesiones (COPIA DE PLANES)
        fechaFin: String          // Calculada según la duración del plan
    ) {
        val userMap = hashMapOf(
            "uid" to uid,
            "nombre_usuario" to nombreUsuario,
            "email" to email,
            "telefono" to telefono,
            "suscripcion_actual" to hashMapOf(
                "id_plan" to idPlan,
                "nombre_plan" to nombrePlan,
                "estado_suscripcion" to "ACTIVA",
                "fecha_inicio_plan" to com.google.firebase.Timestamp.now(),
                "fecha_fin_plan" to fechaFin,
                "limite_sesiones" to limiteSesiones
            ),
            "consumo_actual" to hashMapOf(
                "mes_anio" to "02_2026", // Esto se calcularía dinámicamente
                "sesiones_gastadas" to 0
            )
        )

        db.collection("usuarios").document(uid).set(userMap).await()
    }

    private fun getMesAnioActual(): String {
        val sdf = java.text.SimpleDateFormat("MM_yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}