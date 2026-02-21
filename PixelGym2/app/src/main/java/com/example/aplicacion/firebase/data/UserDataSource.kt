package com.example.aplicacion.firebase.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserDataSource(private val db: FirebaseFirestore) {

    suspend fun saveUserInFirestore(
        uid: String,
        nombreUsuario: String,
        email: String,
        idPlan: String,
        nombrePlan: String,
        limiteSesiones: Int,
        fechaFin: String
    ) {
        val userMap = hashMapOf(
            "uid" to uid,
            "nombre_usuario" to nombreUsuario,
            "email" to email,
            "suscripcion_actual" to hashMapOf(
                "id_plan" to idPlan,
                "nombre_plan" to nombrePlan,
                "estado_suscripcion" to "ACTIVA",
                "fecha_inicio_plan" to com.google.firebase.Timestamp.now(),
                "fecha_fin_plan" to fechaFin,
                "creditos" to limiteSesiones // CAMBIO: Usamos 'creditos' para que coincida con tu modelo Tarifa
            ),
            "consumo_actual" to hashMapOf(
                "mes_anio" to getMesAnioActual(), // Usamos la función que ya tienes abajo
                "sesiones_gastadas" to 0
            )
        )

        // Esto creará la colección "usuarios" si no existe al insertar el primer documento
        db.collection("usuarios").document(uid).set(userMap).await()
    }

    private fun getMesAnioActual(): String {
        val sdf = java.text.SimpleDateFormat("MM_yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}