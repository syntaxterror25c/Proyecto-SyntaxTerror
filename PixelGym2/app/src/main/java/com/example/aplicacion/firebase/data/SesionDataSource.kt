package com.example.aplicacion.firebase.data

import com.example.aplicacion.model.Actividad
import com.example.aplicacion.model.Reserva
import com.example.aplicacion.model.Sesion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SesionDataSource {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getSesiones(): List<Sesion> = try {
        db.collection("sesiones").get().await().toObjects(Sesion::class.java)
    } catch (e: Exception) { emptyList() }

// En SesionDataSource.kt

    suspend fun getMisReservas(userId: String): List<Reserva> = try {
        db.collection("reservas")
            .whereEqualTo("uid", userId)
            .get().await().toObjects(Reserva::class.java)
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun realizarReserva(reserva: Reserva): Boolean = try {
        db.collection("reservas").add(reserva).await()
        true
    } catch (e: Exception) { false }

    suspend fun eliminarReserva(idReserva: String): Boolean = try {
        db.collection("reservas").document(idReserva).delete().await()
        true
    } catch (e: Exception) { false }

    suspend fun getActividades(): List<Actividad> = try {
        db.collection("actividades").get().await().toObjects(Actividad::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun getSesionesPorNombre(nombre: String): List<Sesion> = try {
        db.collection("sesiones")
            .whereEqualTo("nombre_actividad", nombre)
            .get().await().toObjects(Sesion::class.java)
    } catch (e: Exception) { emptyList() }

    // Función necesaria para cargarSesionesPorFecha
    suspend fun getSesionesPorNombreYFecha(nombre: String, fecha: String): List<Sesion> = try {
        db.collection("sesiones")
            .whereEqualTo("nombre_actividad", nombre)
            .whereEqualTo("fecha", fecha)
            .get().await().toObjects(Sesion::class.java)
    } catch (e: Exception) { emptyList() }
}