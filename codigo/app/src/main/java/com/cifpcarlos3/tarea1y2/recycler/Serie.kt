package com.cifpcarlos3.tarea1y2.recycler
import com.cifpcarlos3.tarea1y2.R
// Data class que representa cada serie de la lista.
//
// IMPORTANTE: aquí dejo los sonidos como PARÁMETROS OPCIONALES,
// con valor por defecto 0. Así puedo crear Series sin preocuparme
// todavía de los sonidos y la app compila igual.
//
data class Serie(
    val titulo: String,
    val descripcion: String,
    val imagenResId: Int,
    val soundOnResId: Int = R.raw.beep,
    val soundOffResId: Int = R.raw.beep,
    var esFavorita: Boolean = false
)
