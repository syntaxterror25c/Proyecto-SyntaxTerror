package com.example.aplicacion.model

data class Actividad(
    val id: String = "",
    val nombre: String = "",
    val imagen: String = "",
    val categoria: String = "", // Recuperado
    val descripcion: String = "",
    val creditos_necesarios: Int = 0 // El antiguo 'coste'
)