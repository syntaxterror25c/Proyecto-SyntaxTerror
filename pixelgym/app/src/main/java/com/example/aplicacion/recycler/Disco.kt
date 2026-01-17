package com.example.aplicacion.recycler


data class Disco(
    val titulo: String,
    val autor: String,
    val ano: Int,
    var fav: Boolean,
    val imagenId: Int
)