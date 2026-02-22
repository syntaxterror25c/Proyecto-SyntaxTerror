package com.example.aplicacion.model

data class Sesion(
    val id: String = "",
    val nombre_actividad: String = "",
    val nombre_profesor: String = "",
    val fecha: String = "",
    val hora_inicio: String = "",
    val sala: String = "",
    val imagen_url: String = "",
    val capacidad_maxima: Int = 0, // Nombre corto para Firebase
    val plazas_ocupadas: Int = 0,
    val coste: Int = 0,
    val estado_sesion: String = "ACTIVA"
)