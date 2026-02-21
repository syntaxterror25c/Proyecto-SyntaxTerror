package com.example.aplicacion.recycler

import com.google.firebase.Timestamp

data class Reserva(
    val id_reserva: String = "",
    val id_sesion_reservada: String = "",
    val uid: String = "",
    val nombre_actividad: String = "",
    val fecha_sesion: String = "",
    val hora_inicio: String = "",
    val mes_anio: String = "",
    val estado_reserva: String = "ACTIVA",
    val fecha_creacion_reserva: Timestamp? = null
) {
    fun toSesion() = Sesion(
        id = id_sesion_reservada,
        nombre_actividad = nombre_actividad,
        fecha = fecha_sesion,
        hora_inicio = hora_inicio,
        estado_sesion = "RESERVADA"
    )
}