package com.example.aplicacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.firebase.AuthRepository
import com.example.aplicacion.firebase.GymRepository
import com.example.aplicacion.recycler.Sesion
import com.example.aplicacion.recycler.Reserva
import com.example.aplicacion.recycler.Actividad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GymViewModel(
    private val gymRepository: GymRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _listaSesiones = MutableStateFlow<List<Sesion>>(emptyList())
    val listaSesiones: StateFlow<List<Sesion>> = _listaSesiones

    private val _listaMisReservas = MutableStateFlow<List<Reserva>>(emptyList())
    val listaMisReservas: StateFlow<List<Reserva>> = _listaMisReservas

    private val _reservaStatus = MutableStateFlow<Boolean?>(null)
    val reservaStatus: StateFlow<Boolean?> = _reservaStatus

    private val _listaActividades = MutableStateFlow<List<Actividad>>(emptyList())
    val listaActividades: StateFlow<List<Actividad>> = _listaActividades

    fun cargarActividades() {
        viewModelScope.launch {
            val actividades = gymRepository.fetchActividades()
            _listaActividades.value = actividades
        }
    }

    fun cargarSesionesDeActividad(nombre: String) {
        viewModelScope.launch {
            val sesiones = gymRepository.fetchSesionesPorNombre(nombre)
            _listaSesiones.value = sesiones
        }
    }

    fun cargarCartelera() {
        viewModelScope.launch {
            val sesiones = gymRepository.fetchSesiones()
            _listaSesiones.value = sesiones
        }
    }

    fun cargarMisReservas() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.uid
            if (userId != null) {
                val reservas = gymRepository.fetchMisReservas(userId)
                _listaMisReservas.value = reservas
            }
        }
    }

    fun intentarReserva(sesion: Sesion) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.uid
            if (userId != null) {
                val exito = gymRepository.addReserva(userId, sesion)
                _reservaStatus.value = exito
                if (exito) {
                    cargarSesionesDeActividad(sesion.nombre_actividad)
                    cargarMisReservas()
                }
            }
        }
    }

    fun resetReservaStatus() {
        _reservaStatus.value = null
    }

    // --- FUNCIONES DE MANTENIMIENTO (DENTRO DE LA CLASE) ---

    fun vaciarBaseDeDatos() {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val colecciones = listOf("sesiones", "profesores", "salas", "reservas")

        colecciones.forEach { nombreColeccion ->
            db.collection(nombreColeccion).get().addOnSuccessListener { snapshot ->
                val batch = db.batch()
                for (doc in snapshot) {
                    batch.delete(doc.reference)
                }
                batch.commit().addOnSuccessListener {
                    println("Colección $nombreColeccion vaciada con éxito")
                }
            }
        }
    }

    fun crearDatosPrueba() {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        val catalogoActividades = listOf(
            mapOf("nombre" to "Spinning", "coste" to 1, "imagen" to "im_rec_spinning"),
            mapOf("nombre" to "Cardio", "coste" to 1, "imagen" to "im_rec_cardio"),
            mapOf("nombre" to "Zumba", "coste" to 1, "imagen" to "im_rec_zumba"),
            mapOf("nombre" to "Musculación", "coste" to 1, "imagen" to "im_rec_musculacion"),
            mapOf("nombre" to "Yoga", "coste" to 2, "imagen" to "im_rec_yoga"),
            mapOf("nombre" to "Pilates", "coste" to 2, "imagen" to "im_rec_pilates"),
            mapOf("nombre" to "Crossfit", "coste" to 2, "imagen" to "im_rec_crossfit"),
            mapOf("nombre" to "Fitboxing", "coste" to 2, "imagen" to "im_rec_fitboxing")
        )
        catalogoActividades.forEach { a -> db.collection("actividades").document(a["nombre"].toString()).set(a) }

        val profesores = listOf(
            mapOf("id" to "P01", "nombre" to "Carlos Ruiz", "especialidad" to "Spinning"),
            mapOf("id" to "P02", "nombre" to "Marta Sanz", "especialidad" to "Cardio"),
            mapOf("id" to "P03", "nombre" to "Ivan Box", "especialidad" to "Crossfit"),
            mapOf("id" to "P04", "nombre" to "Laura Punch", "especialidad" to "Fitboxing"),
            mapOf("id" to "P05", "nombre" to "Sonia Zen", "especialidad" to "Yoga"),
            mapOf("id" to "P06", "nombre" to "Elena Core", "especialidad" to "Pilates"),
            mapOf("id" to "P07", "nombre" to "Dani Flow", "especialidad" to "Zumba"),
            mapOf("id" to "P08", "nombre" to "Thor", "especialidad" to "Musculación")
        )

        val salas = listOf("Sala Ciclo", "Zona Fuerza", "Sala Zen", "Estudio 1")

        profesores.forEach { p -> db.collection("profesores").document(p["id"] as String).set(p) }
        salas.forEach { s -> db.collection("salas").add(mapOf("nombre" to s)) }

        for (dia in 22..28) {
            val fechaStr = "$dia/2/2026"
            catalogoActividades.forEachIndexed { index, actividad ->
                val sesionBase = mapOf(
                    "fecha" to fechaStr,
                    "nombre_actividad" to actividad["nombre"],
                    "nombre_profesor" to profesores[index % profesores.size]["nombre"],
                    "sala" to salas.random(),
                    "capacidad_maxima" to 20,
                    "plazas_ocupadas" to 0,
                    "imagen_url" to actividad["imagen"],
                    "creditos_necesarios" to actividad["coste"]
                )
                db.collection("sesiones").add(sesionBase.plus("hora_inicio" to "10:00"))
                db.collection("sesiones").add(sesionBase.plus("hora_inicio" to "18:00"))
            }
        }
    }
}