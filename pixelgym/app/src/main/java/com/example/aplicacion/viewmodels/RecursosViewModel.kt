package com.example.aplicacion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aplicacion.data.RecursosRepositorio
import com.example.aplicacion.recycler.Recurso

class RecursosViewModel : ViewModel() {

    var fechaTemporal: String = ""
    var horaTemporal: String = ""

    fun limpiarDatosTemporales() {
        fechaTemporal = ""
        horaTemporal = ""
    }

    // Corregido: Ahora usa RecursosRepositorio para coincidir con el archivo de datos
    private val repositorio = RecursosRepositorio()

    // Lista maestra que contiene el estado real de todos los recursos (incluyendo favoritos)
    private val _listaRecursosMaster = MutableLiveData<MutableList<Recurso>>()
    val listaRecursosMaster: LiveData<MutableList<Recurso>> get() = _listaRecursosMaster

    private var searchTerm: String = ""
    private var isAscending: Boolean = true

    init {
        // Al inicializar, cargamos los datos del repositorio que ya traen el booleano 'fav' definido
        _listaRecursosMaster.value = repositorio.getRecursosIniciales()
    }

    fun setFilter(query: String?) {
        searchTerm = query ?: ""
        notifyDataChanged()
    }

    fun toggleSort() {
        isAscending = !isAscending
        notifyDataChanged()
    }

    /**
     * Cambia el estado de favorito de un recurso y notifica a los observadores.
     */
    fun toggleFavStatus(recurso: Recurso) {
        val listaActual = _listaRecursosMaster.value
        listaActual?.find { it.id == recurso.id }?.let {
            it.fav = !it.fav
            // Notificamos el cambio para que las listas (Todos y Favoritos) se refresquen
            notifyDataChanged()
        }
    }

    /**
     * Fuerza la notificación del LiveData para que los observadores en los fragmentos reaccionen.
     */
    fun notifyDataChanged() {
        _listaRecursosMaster.value = _listaRecursosMaster.value
    }

    /**
     * Devuelve la lista procesada aplicando:
     * 1. Filtro de Favoritos (si onlyFavs es true)
     * 2. Filtro de búsqueda por nombre o tipo
     * 3. Ordenación alfabética
     */
    fun getProcessedList(onlyFavs: Boolean): List<Recurso> {
        var list = _listaRecursosMaster.value?.toList() ?: emptyList()

        // Gestión del estado 'fav': Si el fragmento pide solo favoritos, filtramos por el campo fav
        if (onlyFavs) {
            list = list.filter { it.fav }
        }

        // Filtro de búsqueda
        if (searchTerm.isNotEmpty()) {
            list = list.filter {
                it.nombre.contains(searchTerm, ignoreCase = true) ||
                        it.tipo.contains(searchTerm, ignoreCase = true)
            }
        }

        // Ordenación
        list = if (isAscending) {
            list.sortedBy { it.nombre.lowercase() }
        } else {
            list.sortedByDescending { it.nombre.lowercase() }
        }

        return list
    }

    // --- MÉTODOS PARA RESERVAFRAGMENT ---

    /**
     * Obtiene los tramos horarios disponibles desde el repositorio.
     */
    fun obtenerHorasLibres(recursoId: Int, fecha: String): List<String> {
        return repositorio.getDisponibilidadParaRecurso(recursoId, fecha)
    }

    /**
     * Envía la solicitud de reserva al repositorio.
     */
    fun confirmarReserva(recursoId: Int, usuarioId: Int, fecha: String, hora: String): Boolean {
        return repositorio.realizarReserva(recursoId, usuarioId, fecha, hora)
    }
}