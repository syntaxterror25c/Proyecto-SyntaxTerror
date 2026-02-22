package com.example.aplicacion.utils

import com.example.aplicacion.R

object ImageMapper {
    /**
     * Convierte el nombre de un String de Firebase al ID del recurso drawable.
     * Centralizado para que si cambias un nombre, solo lo cambies aquí.
     */
    fun getDrawableId(nombreImagen: String?): Int {
        return when (nombreImagen) {
            "im_rec_spinning" -> R.drawable.im_rec_spinning
            "im_rec_yoga" -> R.drawable.im_rec_yoga
            "im_rec_zumba" -> R.drawable.im_rec_zumba
            "im_rec_cardio" -> R.drawable.im_rec_cardio
            "im_rec_musculacion" -> R.drawable.im_rec_musculacion
            "im_rec_pilates" -> R.drawable.im_rec_pilates
            "im_rec_crossfit" -> R.drawable.im_rec_crossfit
            "im_rec_fitboxing" -> R.drawable.im_rec_fitboxing
            else -> R.drawable.im_rec_0default // Imagen por defecto
        }
    }
}