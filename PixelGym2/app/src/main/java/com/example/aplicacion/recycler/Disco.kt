package com.example.aplicacion.recycler


data class Disco(
    val id: String = "", //  ID único del documento en Firestore
    val titulo: String = "",
    val autor: String = "",
    val ano: Int = 0,
    var fav: Boolean = false,
    val imagenId: Int = 0
)