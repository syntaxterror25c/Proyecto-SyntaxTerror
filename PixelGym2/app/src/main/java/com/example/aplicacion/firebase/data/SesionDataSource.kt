package com.example.aplicacion.firebase.data

import com.example.aplicacion.recycler.Sesion
import com.example.aplicacion.recycler.Reserva
import com.example.aplicacion.recycler.Actividad
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SesionDataSource(private val db: FirebaseFirestore) {

    // 1. Obtener catálogo de actividades
    suspend fun getActividades(): List<Actividad> {
        return try {
            val snapshot = db.collection("actividades").get().await()
            snapshot.documents.mapNotNull { doc ->
                Actividad(
                    id = doc.id,
                    nombre = doc.getString("nombre") ?: "",
                    imagen = doc.getString("imagen") ?: "",
                    coste = doc.getLong("coste")?.toInt() ?: 0
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    // 2. Obtener sesiones filtradas (IMPORTANTE: Nombre sincronizado con el Repo)
    suspend fun getSesionesPorNombre(nombreActividad: String): List<Sesion> {
        return try {
            val snapshot = db.collection("sesiones")
                .whereEqualTo("nombre_actividad", nombreActividad)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Sesion::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) { emptyList() }
    }

    // 3. Obtener todas las sesiones (Cartelera)
    suspend fun getSesiones(): List<Sesion> {
        return try {
            val snapshot = db.collection("sesiones").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Sesion::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) { emptyList() }
    }

    // 4. Mis Reservas
    suspend fun getMisReservas(userId: String): List<Reserva> {
        return try {
            val snapshot = db.collection("usuarios")
                .document(userId)
                .collection("reservas_usuario")
                .get()
                .await()
            snapshot.toObjects(Reserva::class.java)
        } catch (e: Exception) { emptyList() }
    }

    // 5. Lógica de transacción
    suspend fun realizarReserva(userId: String, sesion: Sesion): Boolean {
        val sesionRef = db.collection("sesiones").document(sesion.id)
        val reservaRef = db.collection("usuarios").document(userId)
            .collection("reservas_usuario").document()

        return try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(sesionRef)
                val plazasOcupadas = snapshot.getLong("plazas_ocupadas") ?: 0
                val capacidadMax = snapshot.getLong("capacidad_maxima") ?: 0

                if (plazasOcupadas < capacidadMax) {
                    transaction.update(sesionRef, "plazas_ocupadas", plazasOcupadas + 1)
                    val nuevaReserva = hashMapOf(
                        "id_reserva" to reservaRef.id,
                        "id_sesion_reservada" to sesion.id,
                        "nombre_actividad" to sesion.nombre_actividad,
                        "fecha_sesion" to sesion.fecha,
                        "hora_inicio" to sesion.hora_inicio,
                        "estado_reserva" to "ACTIVA",
                        "fecha_creacion_reserva" to com.google.firebase.Timestamp.now()
                    )
                    transaction.set(reservaRef, nuevaReserva)
                    true
                } else { false }
            }.await()
        } catch (e: Exception) { false }
    }
}