package com.example.aplicacion.data

import com.example.aplicacion.R
import com.example.aplicacion.recycler.Recurso

/**
 * Esta clase centraliza el acceso a datos.
 * Contiene tanto los usuarios de prueba como el catálogo de recursos.
 */
class RecursosRepositorio {

    // Simulación de tabla USUARIOS usando el modelo Usuario
    private val usuariosDemo = listOf(
        Usuario(1, "Admin Sistema", "admin", "admin"),
        Usuario(2, "Juan Pérez", "user", "usuario"),
        Usuario(3, "Oloman", "oloman", "usuario")
    )

    /**
     * Función para comprobar login.
     * Unificamos la contraseña a "1234" para todos en este prototipo.
     */
    fun comprobarLogin(user: String, pass: String): Usuario? {
        // Validamos la contraseña universal y buscamos al usuario
        return if (pass == "1234") {
            usuariosDemo.find { it.user.lowercase() == user.lowercase() }
        } else {
            null
        }
    }

    /**
     * Devuelve la disponibilidad simulada para un recurso.
     */
    fun getDisponibilidadParaRecurso(recursoId: Int, fecha: String): List<String> {
        return listOf("09:00 - 10:00", "11:00 - 12:00", "16:00 - 17:00")
    }

    /**
     * Simula la acción de guardar una reserva.
     */
    fun realizarReserva(recursoId: Int, usuarioId: Int, fecha: String, intervalo: String): Boolean {
        return true
    }

    /**
     * Genera la lista inicial de recursos.
     */
    fun getRecursosIniciales(): MutableList<Recurso> {
        return mutableListOf(
            Recurso(1, "Musculación", "Zona de pesas libres.", "Fitness", false, R.drawable.im_rec_musculacion, 40, 5),
            Recurso(2, "Cardio Express", "Cintas y elípticas.", "Fitness", true, R.drawable.im_rec_cardio, 25, 10),
            Recurso(3, "Crossfit Box", "Alta intensidad.", "Deportes", true, R.drawable.im_rec_crossfit, 20, 2),
            Recurso(4, "Fitboxing", "Boxeo funcional.", "Contacto", false, R.drawable.im_rec_fitboxing, 15, 0),
            Recurso(5, "Pilates Studio", "Máquinas Reformer.", "Cuerpo y Mente", false, R.drawable.im_rec_pilates, 10, 8),
            Recurso(6, "Spinning", "Ciclismo indoor.", "Actividades", false, R.drawable.im_rec_spinning, 30, 4),
            Recurso(7, "Yoga", "Asanas y meditación.", "Cuerpo y Mente", false, R.drawable.im_rec_yoga, 20, 3),
            Recurso(8, "Zumba", "Baile fitness.", "Actividades", false, R.drawable.im_rec_zumba, 35, 15)
        )
    }
}