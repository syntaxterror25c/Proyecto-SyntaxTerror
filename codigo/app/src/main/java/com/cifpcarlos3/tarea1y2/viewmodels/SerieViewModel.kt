package com.cifpcarlos3.tarea1y2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cifpcarlos3.tarea1y2.R
import com.cifpcarlos3.tarea1y2.recycler.Serie

/**
 * ViewModel encargado de manejar la lista de series y los favoritos.
 *
 * Este ViewModel sirve para la Tarea 2 (Lista + Favoritos)
 * y será compartido por ListFragment y FavFragment.
 *
 * TODO: más adelante, si quieres, puedes añadir sonidos o imágenes personalizadas.
 */
class SerieViewModel : ViewModel() {

    // LiveData interno que guarda TODAS las series.
    private val _series = MutableLiveData<List<Serie>>()

    // LiveData público solo-lectura para los Fragments.
    val series: LiveData<List<Serie>> get() = _series

    init {
        // Al crear el ViewModel, cargo los datos de ejemplo.
        cargarSeries()
    }

    /**
     * Genera la lista de series de ejemplo.
     * Aquí cada Serie lleva obligatoriamente:
     *  - título
     *  - descripción
     *  - imagenResId  (IMPORTANTE: obligatorio en la data class)
     *
     * Los sonidos son opcionales (defecto = 0), por eso no los pongo todavía.
     */
    private fun cargarSeries() {
        _series.value = listOf(
            Serie(
                titulo = "One Piece",
                descripcion = "Netflix",
                imagenResId = R.drawable.one_peace
            ),
            Serie(
                titulo = "Breaking Bad",
                descripcion = "Netflix",
                imagenResId = R.drawable.breaking_bad
            ),
            Serie(
                titulo = "The Last of Us",
                descripcion = "HBO",
                imagenResId = R.drawable.last_of_us
            ),
            Serie(
                titulo = "The Mandalorian",
                descripcion = "Disney+",
                imagenResId = R.drawable.mandalorian
            ),
            Serie(
                titulo = "Stranger Things",
                descripcion = "Netflix",
                imagenResId = R.drawable.stranger_things
            )
        )
    }

    /**
     * Cambia el estado de favorito: si ya estaba, lo quita; si no, lo añade.
     */
    fun toggleFavorito(serie: Serie) {
        val actuales = _series.value?.toMutableList() ?: mutableListOf()

        val index = actuales.indexOf(serie)
        if (index != -1) {
            val actual = actuales[index]
            // Usamos copy() para modificar solo el campo esFavorita
            actuales[index] = actual.copy(esFavorita = !actual.esFavorita)
            _series.value = actuales
        }
    }

    /**
     * Devuelve únicamente las series marcadas como favoritas.
     */
    fun obtenerFavoritas(): List<Serie> {
        return _series.value?.filter { it.esFavorita } ?: emptyList()
    }
}
