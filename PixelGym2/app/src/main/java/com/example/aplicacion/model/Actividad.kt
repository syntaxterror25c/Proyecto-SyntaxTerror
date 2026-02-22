package com.example.aplicacion.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Actividad(
    @DocumentId
    val id: String = "",
    val nombre: String = "",
    val imagen: String = "",
    val categoria: String = "",
    val descripcion: String = "",
    val coste: Int = 0
)