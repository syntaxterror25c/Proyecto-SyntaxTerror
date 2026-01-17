package com.example.aplicacion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.aplicacion.R // Necesitas esto si inicializas la lista aquí
import com.example.aplicacion.recycler.Sala

class ListViewModel : ViewModel() {

    private val _listaDiscosMaster = MutableLiveData<MutableList<Sala>>()
    val listaDiscosMaster: LiveData<MutableList<Sala>> = _listaDiscosMaster

    private var filterText: String = ""
    private var isAscending: Boolean = true

    init {
        // Cargar datos discos ejemplo
        _listaDiscosMaster.value = mutableListOf(
            Sala("Thriller", "Michael Jackson", 1982, false, R.drawable.im_disco_thriller),
            Sala("Abbey Road", "The Beatles", 1969, true, R.drawable.im_disco_abbey_road),
            Sala("A Night at the Opera", "Queen", 1975, false, R.drawable.im_disco_a_night_at_the_opera),
            Sala("Back in Black", "AC/DC", 1980, true, R.drawable.im_disco_back_in_black),
            Sala("My sharona", "The Knack", 1979, true,R.drawable.im_disco_my_sharona),
            Sala("September", "Earth, wind and Fire", 1979, false,R.drawable.im_disco_september)

        )
    }

    // Actualiza el texto de búsqueda y refresca la vista
    fun setFilter(text: String?) {
        filterText = text ?: ""
        notifyDataChanged()
        }
    // Actualiza el sentido del orden y refresca la vista
    fun toggleSort() {
        isAscending = !isAscending
        notifyDataChanged()
        }

    /* Devuelve la lista filtrada y ordenada */
    fun getProcessedList(onlyFavs: Boolean = false): List<Sala> {
        var list = _listaDiscosMaster.value?.toList() ?: emptyList()

        // Filtrar por favoritos si se pide
        if (onlyFavs) {
            list = list.filter { it.fav }
            }

        // Filtrar por texto de búsqueda (Título o Autor) si existe
        if (filterText.isNotEmpty()) {
            list = list.filter {
                it.titulo.contains(filterText, ignoreCase = true)
                        // Buscar también en el autor
                        // || it.autor.contains(filterText, ignoreCase = true)
                }
            }

        // Ordenar por título
        list = if (isAscending) {
            list.sortedBy { it.titulo.lowercase() }
            } else {
            list.sortedByDescending { it.titulo.lowercase() }
            }

        return list
        }
    fun toggleFavStatus(toggledSala: Sala) {
        val currentList = listaDiscosMaster.value

        if (currentList != null) {

            // buscar disco en la lista maestra
            val positionToUpdate = currentList.indexOf(toggledSala)

            if (positionToUpdate != -1) {

                // actualizar estado  en la lista maestra
                val discoToModify = currentList[positionToUpdate]
                discoToModify.fav = !discoToModify.fav

                // notificar cambio
                notifyDataChanged()
            }
        }
    }

    // forzar LiveData a emitir evento aunque MutableList no haya cambiado
    fun notifyDataChanged() {
        _listaDiscosMaster.postValue(_listaDiscosMaster.value)
    }
}