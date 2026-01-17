package com.example.aplicacion.recycler

import androidx.annotation.DrawableRes

data class Recurso(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val tipo: String,
    var fav: Boolean,
    @DrawableRes val imagen: Int,
    // Estos son los campos que faltaban y causaban el error:
    val capacidadMaxima: Int,
    var cuposReservados: Int = 0
)