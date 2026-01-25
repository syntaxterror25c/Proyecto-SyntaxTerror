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
            Recurso(1, "Musculación", "Zona de pesas libres.", "Fitness", false, R.drawable.im_rec_musculacion, 40, 5, textoDetalles = "La zona de musculación está equipada con pesas libres, bancos y máquinas guiadas para trabajar todos los grupos musculares. Es ideal tanto para entrenamientos de fuerza como de hipertrofia, permitiendo adaptar la carga y el ritmo a cualquier nivel, desde principiantes hasta usuarios avanzados. El espacio está diseñado para entrenar de forma segura y eficiente, favoreciendo una correcta ejecución de los ejercicios."),
            Recurso(2, "Cardio Express", "Cintas y elípticas.", "Fitness", true, R.drawable.im_rec_cardio, 25, 10, textoDetalles = "Área destinada al entrenamiento cardiovascular con cintas de correr, bicicletas y elípticas. Ideal para mejorar la resistencia, quemar calorías y mantener la salud cardiovascular. Pensada para sesiones dinámicas y rápidas, permite ajustar la intensidad según los objetivos de cada usuario."),
            Recurso(3, "Crossfit Box", "Alta intensidad.", "Deportes", true, R.drawable.im_rec_crossfit, 20, 2, textoDetalles = "Espacio preparado para entrenamientos funcionales de alta intensidad basados en fuerza, resistencia y potencia. El Crossfit Box cuenta con material específico como barras, kettlebells y cajas pliométricas, fomentando entrenamientos variados, exigentes y altamente motivadores."),
            Recurso(4, "Fitboxing", "Boxeo funcional.", "Contacto", false, R.drawable.im_rec_fitboxing, 15, 0, textoDetalles = "Actividad que combina movimientos de boxeo con ejercicios funcionales al ritmo de la música. El fitboxing mejora la condición física general, la coordinación y la resistencia, ofreciendo entrenamientos intensos, divertidos y enfocados en la quema de calorías."),
            Recurso(5, "Pilates Studio", "Máquinas Reformer.", "Cuerpo y Mente", false, R.drawable.im_rec_pilates, 10, 8, textoDetalles = "Sala especializada en pilates con máquinas Reformer, enfocada en el control del cuerpo, la postura y la respiración. Ideal para mejorar la flexibilidad, fortalecer el core y prevenir lesiones, ofreciendo un entorno tranquilo y guiado para todos los niveles."),
            Recurso(6, "Spinning", "Ciclismo indoor.", "Actividades", false, R.drawable.im_rec_spinning, 30, 4, textoDetalles = "Sesiones de ciclismo indoor dirigidas y motivadoras, diseñadas para mejorar la resistencia cardiovascular y la fuerza del tren inferior. Las clases se adaptan a diferentes niveles de intensidad, combinando música, ritmo y energía."),
            Recurso(7, "Yoga", "Asanas y meditación.", "Cuerpo y Mente", false, R.drawable.im_rec_yoga, 20, 3, textoDetalles = "Espacio dedicado a la práctica de yoga, combinando posturas (asanas), respiración y meditación. Favorece la relajación, la flexibilidad y el equilibrio mental, siendo ideal para reducir el estrés y mejorar el bienestar general."),
            Recurso(8, "Zumba", "Baile fitness.", "Actividades", false, R.drawable.im_rec_zumba, 35, 15, textoDetalles = "Actividad de baile fitness que mezcla coreografías sencillas con ritmos latinos y actuales. Zumba es ideal para entrenar de forma divertida, mejorar la coordinación y quemar calorías en un ambiente dinámico y social."),
        )
    }
}