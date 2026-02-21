package com.example.aplicacion.firebase

import com.example.aplicacion.firebase.data.SesionDataSource
import com.example.aplicacion.model.Actividad
import com.example.aplicacion.model.Reserva
import com.example.aplicacion.model.Sesion
import kotlinx.coroutines.tasks.await

class GymRepository(private val sesionDataSource: SesionDataSource) {

    // Cambiado a fetchSesiones para el ViewModel
    suspend fun fetchSesiones(): List<Sesion> = sesionDataSource.getSesiones()

    // Cambiado a fetchMisReservas
    suspend fun fetchMisReservas(userId: String): List<Reserva> = sesionDataSource.getMisReservas(userId)

    // Cambiado a addReserva (recibe userId y sesión, y crea la Reserva)
    suspend fun addReserva(userId: String, sesion: Sesion): Boolean {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        // 1. Pedimos un documento nuevo para generar su ID real ANTES de guardar
        val docRef = db.collection("reservas").document()

        val nuevaReserva = Reserva(
            id_reserva = docRef.id, // <--- AQUÍ: Guardamos el ID real de Firestore
            id_sesion_reservada = sesion.id,
            uid = userId,
            nombre_actividad = sesion.nombre_actividad,
            nombre_profesor = sesion.nombre_profesor,
            fecha_sesion = sesion.fecha,
            hora_inicio = sesion.hora_inicio,
            mes_anio = sesion.fecha.substringAfter("/"),
            estado_reserva = "ACTIVA",
            fecha_creacion_reserva = com.google.firebase.Timestamp.now(),
            imagen_url = sesion.imagen_url
        )

        return try {
            docRef.set(nuevaReserva).await() // Usamos .set() con la referencia que ya tiene el ID
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarReserva(idReserva: String): Boolean = sesionDataSource.eliminarReserva(idReserva)

    // Cambiado a fetchSesionesPorNombre
    suspend fun fetchSesionesPorNombre(nombre: String): List<Sesion> = sesionDataSource.getSesionesPorNombre(nombre)

    // Nuevo: El método que faltaba para la búsqueda por fecha
    suspend fun fetchSesionesPorNombreYFecha(nombre: String, fecha: String): List<Sesion> =
        sesionDataSource.getSesionesPorNombreYFecha(nombre, fecha)

    suspend fun fetchActividades(): List<Actividad> = sesionDataSource.getActividades()
}