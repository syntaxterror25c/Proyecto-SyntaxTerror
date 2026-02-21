package com.example.aplicacion.firebase

import com.example.aplicacion.firebase.data.SesionDataSource
import com.example.aplicacion.model.Actividad
import com.example.aplicacion.model.Reserva
import com.example.aplicacion.model.Sesion

class GymRepository(private val sesionDataSource: SesionDataSource) {

    // Cambiado a fetchSesiones para el ViewModel
    suspend fun fetchSesiones(): List<Sesion> = sesionDataSource.getSesiones()

    // Cambiado a fetchMisReservas
    suspend fun fetchMisReservas(userId: String): List<Reserva> = sesionDataSource.getMisReservas(userId)

    // Cambiado a addReserva (recibe userId y sesión, y crea la Reserva)
    suspend fun addReserva(userId: String, sesion: Sesion): Boolean {
        val nuevaReserva = Reserva(
            id_reserva = "", // Firebase generará un ID automático al hacer .add()
            id_sesion_reservada = sesion.id, // O el campo que identifique la sesión
            uid = userId,
            nombre_actividad = sesion.nombre_actividad,
            nombre_profesor = sesion.nombre_profesor,
            fecha_sesion = sesion.fecha,
            hora_inicio = sesion.hora_inicio,
            mes_anio = sesion.fecha.substringAfter("/"), // Ejemplo para sacar "02/2026"
            estado_reserva = "ACTIVA",
            fecha_creacion_reserva = null // O puedes pasarle el timestamp actual
        )
        return sesionDataSource.realizarReserva(nuevaReserva)
    }

    suspend fun eliminarReserva(idReserva: String): Boolean = sesionDataSource.eliminarReserva(idReserva)

    // Cambiado a fetchSesionesPorNombre
    suspend fun fetchSesionesPorNombre(nombre: String): List<Sesion> = sesionDataSource.getSesionesPorNombre(nombre)

    // Nuevo: El método que faltaba para la búsqueda por fecha
    suspend fun fetchSesionesPorNombreYFecha(nombre: String, fecha: String): List<Sesion> =
        sesionDataSource.getSesionesPorNombreYFecha(nombre, fecha)

    suspend fun fetchActividades(): List<Actividad> = sesionDataSource.getActividades()
}