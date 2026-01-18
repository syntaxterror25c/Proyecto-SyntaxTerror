package com.example.aplicacion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aplicacion.data.RecursosRepositorio
import com.example.aplicacion.recycler.Recurso

class RecursosViewModel : ViewModel() {

    private val repositorio = RecursosRepositorio()

    var fechaTemporal: String = ""
    var horaTemporal: String = ""

    private val _listaRecursosMaster = MutableLiveData<MutableList<Recurso>>()
    val listaRecursosMaster: LiveData<MutableList<Recurso>> get() = _listaRecursosMaster

    // Nueva lista para gestionar las reservas realizadas
    private val _misReservas = MutableLiveData<MutableList<Reserva>>(mutableListOf())
    val misReservas: LiveData<MutableList<Reserva>> get() = _misReservas

    private var searchTerm: String = ""
    private var isAscending: Boolean = true

    init {
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

    fun toggleFavStatus(recurso: Recurso) {
        val listaActual = _listaRecursosMaster.value
        listaActual?.find { it.id == recurso.id }?.let {
            it.fav = !it.fav
            notifyDataChanged()
        }
    }

    fun notifyDataChanged() {
        _listaRecursosMaster.value = _listaRecursosMaster.value
    }

    fun getProcessedList(onlyFavs: Boolean): List<Recurso> {
        var list = _listaRecursosMaster.value?.toList() ?: emptyList()
        if (onlyFavs) list = list.filter { it.fav }
        if (searchTerm.isNotEmpty()) {
            list = list.filter {
                it.nombre.contains(searchTerm, ignoreCase = true) ||
                        it.tipo.contains(searchTerm, ignoreCase = true)
            }
        }
        list = if (isAscending) list.sortedBy { it.nombre.lowercase() }
        else list.sortedByDescending { it.nombre.lowercase() }
        return list
    }

    fun obtenerHorasLibres(recursoId: Int, fecha: String): List<String> =
        repositorio.getDisponibilidadParaRecurso(recursoId, fecha)

    fun confirmarReserva(recursoId: Int, usuarioId: Int, fecha: String, hora: String): Boolean {
        val exito = repositorio.realizarReserva(recursoId, usuarioId, fecha, hora)
        if (exito) {
            val recurso = _listaRecursosMaster.value?.find { it.id == recursoId }
            val nuevaReserva = Reserva(
                id = (misReservas.value?.size ?: 0) + 1,
                recursoNombre = recurso?.nombre ?: "Recurso",
                fecha = fecha,
                hora = hora,
                usuarioId = usuarioId
            )
            _misReservas.value?.add(nuevaReserva)
            _misReservas.value = _misReservas.value // Notificar observadores
        }
        return exito
    }

    fun limpiarDatosTemporales() {
        fechaTemporal = ""
        horaTemporal = ""
    }
}