package com.cifpcarlos3.tarea1y2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cifpcarlos3.tarea1y2.R
import com.cifpcarlos3.tarea1y2.recycler.Serie

class ListViewModel : ViewModel() {

    private val _series = MutableLiveData<List<Serie>>()

    val series: LiveData<List<Serie>> get() = _series

    init {
        _series.value = crearSeriesReales()
    }

    //  Lista REAL con las series que tengo en drawable
    private fun crearSeriesReales(): List<Serie> {
        return listOf(

            Serie(
                titulo = "Breaking Bad",
                descripcion = "Un profesor de química se convierte en fabricante de metanfetamina.",
                imagenResId = R.drawable.breaking_bad

            ),

            Serie(
                titulo = "Stranger Things",
                descripcion = "Un grupo de amigos descubre fuerzas sobrenaturales en su pueblo.",
                imagenResId = R.drawable.stranger_things
            ),

            Serie(
                titulo = "The Expanse",
                descripcion = "Intriga y tensión política en un sistema solar colonizado.",
                imagenResId = R.drawable.the_expanse
            )
        )
    }

    fun toggleFavorito(position: Int) {
        val listaActual = _series.value?.toMutableList() ?: return
        val serie = listaActual[position]
        listaActual[position] = serie.copy(esFavorita = !serie.esFavorita)
        _series.value = listaActual
    }

    fun obtenerFavoritas(): List<Serie> {
        return _series.value?.filter { it.esFavorita } ?: emptyList()
    }
}
