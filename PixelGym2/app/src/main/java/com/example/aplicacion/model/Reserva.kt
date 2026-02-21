package com.example.aplicacion.model

import java.security.Timestamp

data class Reserva(
    val id_reserva: String = "",
    val id_sesion_reservada: String = "",
    val uid: String = "",
    val nombre_actividad: String = "",
    val nombre_profesor: String = "", // <--- AÑADIDO
    val fecha_sesion: String = "",
    val hora_inicio: String = "",
    val mes_anio: String = "",
    val estado_reserva: String = "ACTIVA",
    val fecha_creacion_reserva: Timestamp? = null
)