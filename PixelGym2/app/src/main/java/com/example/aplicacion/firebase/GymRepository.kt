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
//    suspend fun addReserva(userId: String, sesion: Sesion): Boolean {
//        val userRef = db.collection("usuarios").document(userId)
//        val sesionRef = db.collection("sesiones").document(sesion.id)
//        val docReservaRef = db.collection("reservas").document()
//
//        return try {
//            db.runTransaction { transaction ->
//                val userSnap = transaction.get(userRef)
//                val sesionSnap = transaction.get(sesionRef)
//
//                val creditosActuales = userSnap.getLong("creditos") ?: 0
//                val plazasOcupadas = sesionSnap.getLong("plazas_ocupadas") ?: 0
//                val capacidadMax = sesionSnap.getLong("capacidad_maxima") ?: 20
//
//                // Validaciones dentro de la transacción
//                if (creditosActuales < sesion.creditos_necesarios) throw Exception("Créditos insuficientes")
//                if (plazasOcupadas >= capacidadMax) throw Exception("Sesión completa")
//
//                // A. Descontar créditos
//                transaction.update(userRef, "creditos", creditosActuales - sesion.creditos_necesarios)
//
//                // B. Actualizar aforo
//                transaction.update(sesionRef, "plazas_ocupadas", plazasOcupadas + 1)
//
//                // C. Crear objeto Reserva
//                val nuevaReserva = Reserva(
//                    id_reserva = docReservaRef.id,
//                    id_sesion_reservada = sesion.id,
//                    uid = userId,
//                    nombre_actividad = sesion.nombre_actividad,
//                    nombre_profesor = sesion.nombre_profesor,
//                    fecha_sesion = sesion.fecha,
//                    hora_inicio = sesion.hora_inicio,
//                    mes_anio = sesion.fecha.substringAfter("/"),
//                    estado_reserva = "ACTIVA",
//                    fecha_creacion_reserva = com.google.firebase.Timestamp.now(),
//                    imagen_url = sesion.imagen_url
//                )
//
//                // D. Guardar reserva
//                transaction.set(docReservaRef, nuevaReserva)
//            }.await()
//            true
//        } catch (e: Exception) {
//            println("DEBUG_APP: Error en reserva -> ${e.message}")
//            false
//        }
//    }

    // En GymRepository.kt
    suspend fun addReserva(userId: String, sesion: Sesion): Boolean {
        val userRef = db.collection("usuarios").document(userId)
        val sesionRef = db.collection("sesiones").document(sesion.id)
        val docReservaRef = db.collection("reservas").document()

        return try {
            db.runTransaction { transaction ->
                val userSnap = transaction.get(userRef)
                val sesionSnap = transaction.get(sesionRef)

                // 1. EXTRAER CRÉDITOS (RUTA ANIDADA)
                // Como en tu captura 'creditos' está dentro de 'suscripcion_actual'
                val suscripcion = userSnap.get("suscripcion_actual") as? Map<String, Any>
                val creditosActuales = (suscripcion?.get("creditos") as? Number)?.toLong() ?: 0L

                // 2. EXTRAER DATOS SESIÓN
                val plazasOcupadas = sesionSnap.getLong("plazas_ocupadas") ?: 0L
                val capacidadMax = sesionSnap.getLong("capacidad_maxima") ?: 20L

                // LOGS DE CONTROL
                println("DEBUG_RESERVA: Usuario ID: ${userId}")
                println("DEBUG_RESERVA: Créditos actuales en mapa: $creditosActuales")
                println("DEBUG_RESERVA: Créditos que pide la sesión: ${sesion.creditos_necesarios}")

                // 3. VALIDACIONES
                if (creditosActuales < sesion.creditos_necesarios) {
                    throw Exception("CRÉDITOS INSUFICIENTES")
                }
                if (plazasOcupadas >= capacidadMax) {
                    throw Exception("SESIÓN LLENA")
                }

                // 4. ACTUALIZACIONES (IMPORTANTE: usamos la ruta con punto para el mapa)
                transaction.update(userRef, "suscripcion_actual.creditos", creditosActuales - sesion.creditos_necesarios)
                transaction.update(sesionRef, "plazas_ocupadas", plazasOcupadas + 1)

                // 5. CREAR OBJETO RESERVA
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
                    imagen_url = sesion.imagen_url
                )

                transaction.set(docReservaRef, nuevaReserva)
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
                // NOTA: Para devolver créditos exactos, deberías tener guardado
                // cuántos costó en el objeto Reserva. Aquí devolvemos 1 por defecto.
                transaction.update(userRef, "creditos", FieldValue.increment(1))
                transaction.update(sesionRef, "plazas_ocupadas", FieldValue.increment(-1))
                transaction.delete(reservaRef)
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }
}