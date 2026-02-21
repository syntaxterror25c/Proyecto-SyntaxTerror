package com.example.aplicacion.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.firebase.AuthRepository
import com.example.aplicacion.firebase.GymRepository
import com.example.aplicacion.model.Sesion
import com.example.aplicacion.model.Actividad
import com.example.aplicacion.model.Reserva
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    // Nuevo: Cargar sesiones filtrando por nombre y por la fecha seleccionada
    fun cargarSesionesPorFecha(nombre: String, fecha: String) {
        viewModelScope.launch {
            val sesiones = gymRepository.fetchSesionesPorNombreYFecha(nombre, fecha)
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

    fun anularReserva(reserva: Reserva) {
        viewModelScope.launch {
            // Llamamos al repo para borrar usando el ID que ahora sí estará bien guardado
            val exito = gymRepository.eliminarReserva(reserva.id_reserva)
            if (exito) {
                // Si se borra en la nube, volvemos a cargar la lista local para que desaparezca de la pantalla
                cargarMisReservas()
            }
        }
    }
    // --- FUNCIONES DE MANTENIMIENTO  ---

    fun resetTotalGimnasioPruebas() {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        // 1. LISTA TOTAL: Borramos todo para empezar de cero absoluto
        val colecciones = listOf("sesiones", "profesores", "salas", "reservas", "actividades", "tarifas", "usuarios")

        var contadorColecciones = 0

        colecciones.forEach { nombreColeccion ->
            db.collection(nombreColeccion).get().addOnSuccessListener { snapshot ->
                val batch = db.batch()
                for (doc in snapshot) {
                    batch.delete(doc.reference)
                }

                batch.commit().addOnCompleteListener {
                    contadorColecciones++

                    // 2. SOLO cuando se ha borrado la ÚLTIMA colección, grabamos los datos
                    if (contadorColecciones == colecciones.size) {
                        println("--- Base de datos limpia. Grabando tus datos de prueba... ---")
                        ejecutarGrabacionDatosPrueba()
                    }
                }
            }.addOnFailureListener {
                contadorColecciones++
                if (contadorColecciones == colecciones.size) ejecutarGrabacionDatosPrueba()
            }
        }
    }

    private fun ejecutarGrabacionDatosPrueba() {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        // --- 1. ACTIVIDADES ---
        val catalogoActividades = listOf(
            mapOf("nombre" to "Spinning", "coste" to 1, "imagen" to "im_rec_spinning", "categoria" to "Cardio", "descripcion" to "Clase de ciclismo indoor de alta intensidad."),
            mapOf("nombre" to "Cardio", "coste" to 1, "imagen" to "im_rec_cardio", "categoria" to "Cardio", "descripcion" to "Mejora tu resistencia cardiovascular."),
            mapOf("nombre" to "Zumba", "coste" to 1, "imagen" to "im_rec_zumba", "categoria" to "Baile", "descripcion" to "Ejercicio divertido a ritmo de música latina."),
            mapOf("nombre" to "Musculación", "coste" to 1, "imagen" to "im_rec_musculacion", "categoria" to "Fuerza", "descripcion" to "Entrenamiento libre en sala de máquinas."),
            mapOf("nombre" to "Yoga", "coste" to 2, "imagen" to "im_rec_yoga", "categoria" to "Cuerpo-Mente", "descripcion" to "Relajación y estiramientos profundos."),
            mapOf("nombre" to "Pilates", "coste" to 2, "imagen" to "im_rec_pilates", "categoria" to "Cuerpo-Mente", "descripcion" to "Fortalecimiento del core y postura."),
            mapOf("nombre" to "Crossfit", "coste" to 2, "imagen" to "im_rec_crossfit", "categoria" to "Fuerza", "descripcion" to "WODs de alta intensidad funcional."),
            mapOf("nombre" to "Fitboxing", "coste" to 2, "imagen" to "im_rec_fitboxing", "categoria" to "Cardio", "descripcion" to "Golpeo al saco y ejercicios funcionales.")
        )

        catalogoActividades.forEach { a ->
            db.collection("actividades").document(a["nombre"].toString()).set(a)
        }

        // --- 2. PROFESORES ---
        val profesores = listOf(
            mapOf("id" to "P01", "nombre" to "Carlos Ruiz"),
            mapOf("id" to "P02", "nombre" to "Marta Sanz"),
            mapOf("id" to "P03", "nombre" to "Ivan Box"),
            mapOf("id" to "P04", "nombre" to "Laura Punch"),
            mapOf("id" to "P05", "nombre" to "Sonia Zen"),
            mapOf("id" to "P06", "nombre" to "Elena Core"),
            mapOf("id" to "P07", "nombre" to "Dani Flow"),
            mapOf("id" to "P08", "nombre" to "Thor")
        )
        profesores.forEach { p -> db.collection("profesores").document(p["id"] as String).set(p) }

        // --- 3. SALAS ---
        val salas = listOf("Sala Ciclo", "Zona Fuerza", "Sala Zen", "Estudio 1")
        salas.forEach { s -> db.collection("salas").document(s).set(mapOf("nombre" to s)) }

        // --- 4. SESIONES (Generación Automática) ---
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())

        for (dia in 22..28) {
            val cal = java.util.Calendar.getInstance().apply { set(2026, java.util.Calendar.FEBRUARY, dia) }
            val fechaStr = sdf.format(cal.time)

            catalogoActividades.forEachIndexed { index, actividad ->
                val nombreAct = actividad["nombre"].toString()
                val idMañana = "${nombreAct}_${fechaStr.replace("/", "-")}_1000"
                val idTarde = "${nombreAct}_${fechaStr.replace("/", "-")}_1800"

                val sesionBase = mapOf(
                    "fecha" to fechaStr,
                    "nombre_actividad" to nombreAct,
                    "nombre_profesor" to profesores[index % profesores.size]["nombre"],
                    "sala" to salas.random(),
                    "capacidad_maxima" to 20,
                    "plazas_ocupadas" to 0,
                    "imagen_url" to actividad["imagen"],
                    "creditos_necesarios" to actividad["coste"],
                    "estado_sesion" to "ACTIVA"
                )

                db.collection("sesiones").document(idMañana).set(sesionBase.plus("hora_inicio" to "10:00"))
                db.collection("sesiones").document(idTarde).set(sesionBase.plus("hora_inicio" to "18:00"))
            }
        }

        // --- 5. TARIFAS ---
        val tarifas = listOf(
            mapOf("nombre" to "Simple", "creditos" to 4, "precio" to 15.99, "descripcion" to "Ideal para probar el gimnasio"),
            mapOf("nombre" to "Básico", "creditos" to 8, "precio" to 28.79, "descripcion" to "Dos créditos por semana"),
            mapOf("nombre" to "Estándar", "creditos" to 12, "precio" to 40.79, "descripcion" to "Tres créditos por semana"),
            mapOf("nombre" to "VIP", "creditos" to 16, "precio" to 51.19, "descripcion" to "Cuatro créditos por semana"),
            mapOf("nombre" to "Infinito", "creditos" to 999, "precio" to 59.99, "descripcion" to "Acceso total ilimitado")
        )

        tarifas.forEach { t ->
            db.collection("tarifas").document(t["nombre"].toString()).set(t)
        }
    }
}