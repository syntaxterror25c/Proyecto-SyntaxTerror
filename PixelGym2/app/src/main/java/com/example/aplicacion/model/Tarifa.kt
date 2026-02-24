package com.example.aplicacion.models

data class Tarifa(
    val nombre: String = "",
    val creditos: Int = 0,
    val precio: Double = 0.0
) {
    // Esto es lo que verá el usuario en el Spinner
    override fun toString(): String = "$nombre - $creditos créditos - ${String.format("%.2f", precio)}€"
}