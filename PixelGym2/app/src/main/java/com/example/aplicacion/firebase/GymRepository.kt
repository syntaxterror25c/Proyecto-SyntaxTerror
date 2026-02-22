package com.example.aplicacion.firebase

import com.example.aplicacion.firebase.data.SesionDataSource
import com.example.aplicacion.model.Actividad
import com.example.aplicacion.model.Reserva
import com.example.aplicacion.model.Sesion
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

class GymRepository(private val sesionDataSource: SesionDataSource) {

    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchSesiones(): List<Sesion> = sesionDataSource.getSesiones()

    suspend fun fetchMisReservas(userId: String): List<Reserva> = sesionDataSource.getMisReservas(userId)

    suspend fun fetchActividades(): List<Actividad> = sesionDataSource.getActividades()

    suspend fun fetchSesionesPorNombre(nombre: String): List<Sesion> = sesionDataSource.getSesionesPorNombre(nombre)

    suspend fun fetchSesionesPorNombreYFecha(nombre: String, fecha: String): List<Sesion> =
        sesionDataSource.getSesionesPorNombreYFecha(nombre, fecha)

    /**
     * Obtiene los créditos actuales del usuario
     */
    suspend fun fetchCreditosUsuario(userId: String): Int {
        return try {
            val snapshot = db.collection("usuarios").document(userId).get().await()
            snapshot.getLong("creditos")?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * PROCESO DE RESERVA COMPLETO (Transacción)
     * 1. Descuenta créditos al usuario
     * 2. Suma 1 al aforo de la sesión
     * 3. Crea el documento de reserva
     */

    suspend fun addReserva(userId: String, sesion: Sesion): Boolean {
        val userRef = db.collection("usuarios").document(userId)
        val sesionRef = db.collection("sesiones").document(sesion.id)
        val docReservaRef = db.collection("reservas").document()

        return try {
            db.runTransaction { transaction ->
                val userSnap = transaction.get(userRef)
                val sesionSnap = transaction.get(sesionRef)

                // 1. EXTRAER CRÉDITOS (Búsqueda flexible)
                val suscripcion = userSnap.get("suscripcion_actual") as? Map<*, *>
                val creditosActuales = (suscripcion?.get("creditos") as? Number)?.toLong()
                    ?: userSnap.getLong("creditos")
                    ?: 0L

                // 2. EXTRAER DATOS SESIÓN
                val plazasOcupadas = sesionSnap.getLong("plazas_ocupadas") ?: 0L
                val capacidadMax = sesionSnap.getLong("capacidad_maxima") ?: 20L

                // 3. VALIDACIONES
                if (creditosActuales < sesion.coste) throw Exception("CRÉDITOS INSUFICIENTES")
                if (plazasOcupadas >= capacidadMax) throw Exception("SESIÓN LLENA")

                // 4. ACTUALIZACIÓN DINÁMICA DE CRÉDITOS
                // Si el campo existe dentro del mapa, lo actualizamos ahí. Si no, a la raíz.
                if (userSnap.contains("suscripcion_actual.creditos")) {
                    transaction.update(userRef, "suscripcion_actual.creditos", creditosActuales - sesion.coste)
                } else {
                    transaction.update(userRef, "creditos", creditosActuales - sesion.coste)
                }

                // 5. ACTUALIZACIÓN DE SESIÓN Y CREACIÓN DE RESERVA
                transaction.update(sesionRef, "plazas_ocupadas", plazasOcupadas + 1)

                val nuevaReserva = Reserva(
                    id_reserva = docReservaRef.id,
                    id_sesion_reservada = sesion.id,
                    uid = userId,
                    nombre_actividad = sesion.nombre_actividad,
                    nombre_profesor = sesion.nombre_profesor,
                    fecha_sesion = sesion.fecha,
                    hora_inicio = sesion.hora_inicio,
                    mes_anio = sesion.fecha.substringAfter("/"),
                    estado_reserva = "ACTIVA",
                    fecha_creacion_reserva = com.google.firebase.Timestamp.now(),
                    imagen_url = sesion.imagen_url,
                    coste = sesion.coste
                )

                transaction.set(docReservaRef, nuevaReserva)
                null // Retorno explícito para cerrar la lambda de la transacción
            }.await()
            true
        } catch (e: Exception) {
            println("ERROR_CRITICO: ${e.message}")
            false
        }
    }

    /**
     * ELIMINAR RESERVA (Transacción inversa)
     * Devuelve el crédito y libera la plaza
     */
    suspend fun eliminarReserva(reserva: Reserva): Boolean {
        val userRef = db.collection("usuarios").document(reserva.uid)
        val sesionRef = db.collection("sesiones").document(reserva.id_sesion_reservada)
        val reservaRef = db.collection("reservas").document(reserva.id_reserva)

        return try {
            db.runTransaction { transaction ->
                // Usamos el coste que guardamos en la reserva convertido a Long por Firestore
                val costeADevolver = reserva.coste.toLong()
                // ACTUALIZACIÓN DE CRÉDITOS
                // Usamos FieldValue.increment con el coste real,
                transaction.update(userRef, "suscripcion_actual.creditos", FieldValue.increment(costeADevolver))
                // CTUALIZACIÓN DE SESIÓN: Restamos 1 a las plazas ocupadas
                transaction.update(sesionRef, "plazas_ocupadas", FieldValue.increment(-1))
                // ELIMINACIÓN: Borramos el ticket de reserva
                transaction.delete(reservaRef)
            }.await()
            true
        } catch (e: Exception) {
            println("DEBUG_ELIMINAR: Error al anular -> ${e.message}")
            false
        }
    }
}