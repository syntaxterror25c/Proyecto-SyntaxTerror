package com.example.aplicacion.firebase

import com.example.aplicacion.firebase.data.SesionDataSource
import com.example.aplicacion.recycler.Actividad
import com.example.aplicacion.recycler.Reserva
import com.example.aplicacion.recycler.Sesion

class GymRepository(private val sesionDataSource: SesionDataSource) {

    suspend fun fetchSesiones(): List<Sesion> =
        sesionDataSource.getSesiones()

    suspend fun fetchMisReservas(userId: String): List<Reserva> =
        sesionDataSource.getMisReservas(userId)

    suspend fun addReserva(userId: String, sesion: Sesion): Boolean =
        sesionDataSource.realizarReserva(userId, sesion)

    suspend fun fetchActividades(): List<Actividad> =
        sesionDataSource.getActividades()

    suspend fun fetchSesionesPorNombre(nombre: String): List<Sesion> =
        sesionDataSource.getSesionesPorNombre(nombre)
}