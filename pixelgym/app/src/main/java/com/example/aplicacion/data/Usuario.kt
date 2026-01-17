package com.example.aplicacion.data


/**
 * Representa al usuario que ha iniciado sesión en el sistema.
 */
data class Usuario(
    val id: Int,
    val nombre: String,
    val user: String,
    val rol: String = "usuario"
)