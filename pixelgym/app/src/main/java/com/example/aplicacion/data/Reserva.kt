package com.example.aplicacion.viewmodels

// Definición de la clase que el adaptador necesita para compilar
data class Reserva(
    val id: Int,
    val recursoNombre: String,
    val fecha: String,
    val hora: String,
    val usuarioId: Int
)