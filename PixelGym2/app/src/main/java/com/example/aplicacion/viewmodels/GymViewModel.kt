package com.example.aplicacion.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.R
import com.example.aplicacion.firebase.AuthRepository
import com.example.aplicacion.firebase.GymRepository
import com.example.aplicacion.model.Sesion
import com.example.aplicacion.model.Actividad
import com.example.aplicacion.model.Reserva
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class GymViewModel(
    private val gymRepository: GymRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // 1 LISTAS Y ESTADOS
    private var todasLasActividades: List<Actividad> = emptyList()
    private var todasLasReservas: List<Reserva> = emptyList()

    private val _listaActividades = MutableStateFlow<List<Actividad>>(emptyList())
    val listaActividades: StateFlow<List<Actividad>> = _listaActividades

    private val _listaSesiones = MutableStateFlow<List<Sesion>>(emptyList())
    val listaSesiones: StateFlow<List<Sesion>> = _listaSesiones

    private val _listaMisReservas = MutableStateFlow<List<Reserva>>(emptyList())
    val listaMisReservas: StateFlow<List<Reserva>> = _listaMisReservas

    private val _reservaStatus = MutableStateFlow<Boolean?>(null)
    val reservaStatus: StateFlow<Boolean?> = _reservaStatus

    // Estado del filtro y orden
    private var queryActual: String = ""
    private var esAscendente: Boolean = true

    private val _usuarioLogueado = MutableStateFlow<Map<String, Any>?>(null)
    val usuarioLogueado: StateFlow<Map<String, Any>?> = _usuarioLogueado

    // 2 LÓGICA DE FILTRADO Y ORDENACIÓN

    fun cargarDatosUsuarioActual() {
        viewModelScope.launch {
            // Obtenemos el ID del usuario desde AuthRepository
            val userId = authRepository.getCurrentUser()?.uid

            if (userId != null) {
                // Llamamos a la función que acabamos de crear en el Repository
                val datos = gymRepository.fetchUserData(userId)

                // Actualizamos el flujo de datos para que el Fragment lo reciba
                _usuarioLogueado.value = datos

                println("DEBUG_VIEWMODEL: Datos de usuario cargados: ${datos?.get("nombre")}")
            } else {
                println("DEBUG_VIEWMODEL: No hay usuario autenticado")
            }
        }
    }
    fun cargarActividades() {
        viewModelScope.launch {
            // Cargamos del repo y guardamos en la "copia de seguridad"
            todasLasActividades = gymRepository.fetchActividades()
            aplicarFiltrosYOrden()
        }
    }

    fun setFilter(query: String?) {
        queryActual = query ?: ""
        aplicarFiltrosYOrden()
    }

    fun toggleSort() {
        esAscendente = !esAscendente
        aplicarFiltrosYOrden()
    }

    private fun aplicarFiltrosYOrden() {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())

        // --- PROCESAR ACTIVIDADES  ---
        var resActividades = todasLasActividades
        if (queryActual.isNotEmpty()) {
            resActividades = resActividades.filter {
                it.nombre.contains(queryActual, ignoreCase = true)
            }
        }
        _listaActividades.value = if (esAscendente) resActividades.sortedBy { it.nombre }
        else resActividades.sortedByDescending { it.nombre }

        // --- PROCESAR RESERVAS  ---
        var resReservas = todasLasReservas
        if (queryActual.isNotEmpty()) {
            resReservas = resReservas.filter {
                it.nombre_actividad.contains(queryActual, ignoreCase = true) ||
                        it.nombre_profesor.contains(queryActual, ignoreCase = true)
            }
        }

        // Ordenamos por FECHA_SESION convirtiendo el String a Date para que sea cronológico
        _listaMisReservas.value = if (esAscendente) {
            resReservas.sortedBy { try { sdf.parse(it.fecha_sesion) } catch (e: Exception) { null } }
        } else {
            resReservas.sortedByDescending { try { sdf.parse(it.fecha_sesion) } catch (e: Exception) { null } }
        }
    }


    // 3 RESTO DE FUNCIONES (Sesiones, Reservas, etc......)

    fun cargarSesionesDeActividad(nombre: String) {
        viewModelScope.launch {
            val sesiones = gymRepository.fetchSesionesPorNombre(nombre)
            _listaSesiones.value = sesiones
        }
    }

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
                // Traemos los datos de Firebase
                val reservasDesdeFirebase = gymRepository.fetchMisReservas(userId)

                // ACTUALIZAMOS LA COPIA DE SEGURIDAD (Sin esto, el filtro no funciona)
                todasLasReservas = reservasDesdeFirebase

                // Ejecutamos la lógica de orden y filtro para que se vea en pantalla
                aplicarFiltrosYOrden()
            }
        }
    }

    fun intentarReserva(sesion: Sesion) {
        _reservaStatus.value = null

        // Usamos Dispatchers.IO para que la red NO bloquee la interfaz
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val userId = authRepository.getCurrentUser()?.uid ?: return@launch

            println("DEBUG_VIEWMODEL: Lanzando reserva en hilo secundario...")
            val exito = gymRepository.addReserva(userId, sesion)

            // IMPORTANTE: Los cambios de UI (StateFlow) DEBEN volver al hilo principal
            launch(kotlinx.coroutines.Dispatchers.Main) {
                println("DEBUG_VIEWMODEL: Volviendo al Main para avisar al Fragment: $exito")
                _reservaStatus.value = exito

                if (exito) {
                    // Refrescamos datos pero sin bloquear
                    cargarSesionesDeActividad(sesion.nombre_actividad)
                    cargarMisReservas()
                }
            }
        }
    }

    fun resetReservaStatus() {
        _reservaStatus.value = null
    }

    // Actualiza esta función en tu GymViewModel.kt
    fun anularReserva(reserva: Reserva) {
        viewModelScope.launch {
            // Llamamos al repo pasando el objeto reserva completo
            // El repo se encargará de:
            // Devolver +1 crédito al usuario
            // Hacer -1 en plazas_ocupadas de la sesión
            // Borrar el documento de la reserva
            val exito = gymRepository.eliminarReserva(reserva)

            if (exito) {
                // Si sale bien, refrescamos la lista de "Mis Reservas" para que desaparezca
                cargarMisReservas()
                // Opcional: refrescar sesiones por si el usuario está viendo la lista
                // y quiere ver que ahora hay un hueco libre más
                cargarCartelera()
            }
        }
    }
    // --- FUNCIONES DE MANTENIMIENTO  ---

    fun resetTotalGimnasioPruebas() {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        // 1 LISTA TOTAL: Borramos todo para empezar de cero absoluto
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

        // --- 1 ACTIVIDADES -------
        val catalogoActividades = listOf(
            mapOf("nombre" to "Spinning", "coste" to 1, "imagen" to "im_rec_spinning", "categoria" to R.string.info_categoria, "descripcion" to R.string.spinning_about),
            mapOf("nombre" to "Cardio", "coste" to 1, "imagen" to "im_rec_cardio", "categoria" to R.string.info_categoria, "descripcion" to R.string.cardio_about),
            mapOf("nombre" to "Zumba", "coste" to 1, "imagen" to "im_rec_zumba", "categoria" to R.string.baile, "descripcion" to R.string.zumba_about),
            mapOf("nombre" to R.string.acti_musculacion, "coste" to 1, "imagen" to "im_rec_musculacion", "categoria" to R.string.fuerza, "descripcion" to R.string.musculacion_about),
            mapOf("nombre" to "Yoga", "coste" to 5, "imagen" to "im_rec_yoga", "categoria" to R.string.cuerpo_mente, "descripcion" to R.string.yoga_about),
            mapOf("nombre" to R.string.acti_pilates, "coste" to 2, "imagen" to "im_rec_pilates", "categoria" to R.string.cuerpo_mente, "descripcion" to R.string.pilates_about),
            mapOf("nombre" to "Crossfit", "coste" to 2, "imagen" to "im_rec_crossfit", "categoria" to R.string.fuerza, "descripcion" to R.string.crossfit_about),
            mapOf("nombre" to "Fitboxing", "coste" to 5, "imagen" to "im_rec_fitboxing", "categoria" to R.string.info_categoria, "descripcion" to R.string.fitboxing_about)
        )

        catalogoActividades.forEach { a ->
            db.collection("actividades").document(a["nombre"].toString()).set(a)
        }

        // --- 2 PROFESORES ------
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

        // --- 3 SALAS ----------
        val salas = listOf("Sala Ciclo", "Zona Fuerza", "Sala Zen", "Estudio 1")
        salas.forEach { s -> db.collection("salas").document(s).set(mapOf("nombre" to s)) }

        // --- 4 SESIONES (Generación Automática) ----------------------------------
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
                    "capacidad_maxima" to 2,
                    "plazas_ocupadas" to 0,
                    "imagen_url" to actividad["imagen"],
                    "coste" to actividad["coste"],
                    "estado_sesion" to "ACTIVA"
                )

                db.collection("sesiones").document(idMañana).set(sesionBase.plus("hora_inicio" to "10:00"))
                db.collection("sesiones").document(idTarde).set(sesionBase.plus("hora_inicio" to "18:00"))
            }
        }

        // --- 5 TARIFAS ----------------
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