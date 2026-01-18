package es.cifpcarlos3.ui;

import es.cifpcarlos3.dao.impl.*;
import es.cifpcarlos3.model.*;
import es.cifpcarlos3.model.enums.TipoSala;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class MenuMantenimientoPixelGym {
    private static final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
    private static final PlanPrecioDAOImpl planPrecioDAO = new PlanPrecioDAOImpl();

    private static final ProfesorDAOImpl profesorDAO = new ProfesorDAOImpl();
    private static final SalaDAOImpl salaDAO = new SalaDAOImpl();
    private static final SesionDAOImpl sesionDAO = new SesionDAOImpl();
    private static final TipoActividadDAOImpl tipoDAO = new TipoActividadDAOImpl();
    private static final ActividadConfiguradaDAOImpl horarioDAO = new ActividadConfiguradaDAOImpl();
    private static final Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion;
        do {
            System.out.println("\n********** PIXEL GYM - PANEL DE CONTROL **********");
            System.out.println("1. GESTIÓN DE RECURSOS (Altas de Usuarios, Profesores, Salas, Tipos)");
            System.out.println("2. CONFIGURACIÓN DE HORARIOS (Plantillas)");
            System.out.println("3. CONSULTAS Y LISTADOS (Ver qué hay)");
            System.out.println("4. GENERAR SESIONES DEL MES (Motor)");
            System.out.println("5. GESTIÓN DIARIA (Cancelar/Sustituciones)");
            System.out.println("0. SALIR");
            System.out.print("Elige una categoría: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1 -> menuGestionRecursos();
                case 2 -> menuNuevaActividadConfigurada();
                case 3 -> menuListados();
                case 4 -> menuGenerarSesionesMes();
                case 5 -> menuGestionSesiones();
                case 0 -> System.out.println("¡Hasta pronto!");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    // --- MÉTODO QUE FALTABA ---
    private static void menuGestionRecursos() {
        System.out.println("\n--- GESTIÓN DE RECURSOS ---");
        System.out.println("1. Registrar nuevo Socio (con Plan)");
        System.out.println("2. Registrar nuevo Plan de Precios");
        System.out.println("3. Registrar nuevo Profesor");
        System.out.println("4. Registrar nueva Sala");
        System.out.println("5. Registrar nuevo Tipo de Actividad");
        System.out.println("0. Volver");
        System.out.print("Opción: ");

        int opt = leerEntero();
        switch (opt) {
            case 1 -> menuNuevoUsuario();
            case 2 -> menuNuevoPlanPrecio();
            case 3 -> menuNuevoProfesor();
            case 4 -> menuNuevaSala();
            case 5 -> menuNuevoTipoActividad();
        }
    }

    private static void menuListados() {
        System.out.println("\n--- SUBMENÚ DE CONSULTAS ---");
        System.out.println("1. Estado de socios (Usuarios + Planes)");
        System.out.println("2. Listado de Profesores");
        System.out.println("3. Listado de Salas");
        System.out.println("4. Listado de Tipos de Actividad");
        System.out.println("5. Horarios semanal (Plantilla Detallada)");
        System.out.println("6. Sesiones calendario (Ocupación Real)");
        System.out.println("7. Ver reservas de un socio");
        System.out.println("0. Volver");
        System.out.print("Selecciona: ");

        int subOpcion = leerEntero();
        switch (subOpcion) {
            case 1 -> listarUsuariosYPlanes();
            case 2 -> listarProfesores();
            case 3 -> listarSalas();
            case 4 -> listarTiposActividad();
            case 5 -> listarHorario();
            case 6 -> mostrarListadoSesiones();
            case 7 -> listarReservasSocio();
        }
    }

    // --- MÉTODOS DE REGISTRO (ALTAS) ---

    private static void menuNuevoProfesor() {
        System.out.println("\n--- NUEVO PROFESOR ---");
        Profesor p = new Profesor();
        System.out.print("Nombre: ");
        p.setNombre(teclado.nextLine());
        System.out.print("Teléfono: ");
        p.setTelefono(teclado.nextLine());
        System.out.print("Especialidad: ");
        p.setEspecialidad(teclado.nextLine());

        try {
            profesorDAO.insertar(p);
            System.out.println("✅ Profesor guardado.");
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void menuNuevoUsuario() {
        System.out.println("\n--- REGISTRO COMPLETO DE SOCIO ---");
        Usuario nuevo = new Usuario();

        System.out.print("Nombre completo: ");
        nuevo.setNombre(teclado.nextLine());

        System.out.print("Email: ");
        nuevo.setEmail(teclado.nextLine());

        System.out.print("Teléfono: ");
        nuevo.setTelefono(teclado.nextLine());

        System.out.print("Contraseña temporal: ");
        nuevo.setPasswordHash(teclado.nextLine());

        // Mostramos los planes disponibles para que elijas uno
        listarPlanesPrecios();
        System.out.print("Selecciona el ID del Plan para este socio: ");
        int idPlan = leerEntero();

        try {
            usuarioDAO.registrarUsuarioConPlan(nuevo, idPlan);
            System.out.println("✅ ¡Perfecto! Usuario creado y plan activado correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error en el alta combinada: " + e.getMessage());
        }
    }
    private static void menuNuevaSala() {
        System.out.println("\n--- NUEVA SALA ---");
        Sala s = new Sala();
        System.out.print("Nombre de la sala: ");
        s.setNombre(teclado.nextLine());
        System.out.print("Capacidad máxima: ");
        s.setCapacidadMaxima(leerEntero());
        System.out.print("Tipo (1- CLASES, 2- MAQUINAS): ");
        int tipo = leerEntero();
        s.setTipo(tipo == 1 ? TipoSala.CLASES : TipoSala.MAQUINAS);

        try {
            salaDAO.insertar(s);
            System.out.println("✅ Sala registrada.");
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void menuNuevoTipoActividad() {
        System.out.println("\n--- NUEVO TIPO DE ACTIVIDAD ---");
        TipoActividad t = new TipoActividad();
        System.out.print("Nombre (ej. Yoga): ");
        t.setNombre(teclado.nextLine());
        System.out.print("Descripción: ");
        t.setDescripcion(teclado.nextLine());

        try {
            tipoDAO.insertar(t);
            System.out.println("✅ Tipo de actividad creado.");
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void menuNuevoPlanPrecio() {
        System.out.println("\n--- NUEVO PLAN DE PRECIOS ---");
        PlanPrecio nuevoPlan = new PlanPrecio();

        System.out.print("Nombre del plan (ej. Premium): ");
        nuevoPlan.setNombrePlan(teclado.nextLine());

        System.out.print("Precio mensual: ");
        nuevoPlan.setPrecioMensual(leerDouble()); // El método que valida decimales

        System.out.print("Límite de actividades (0 para ilimitado): ");
        nuevoPlan.setLimiteActividades(leerEntero());

        try {
            // Usamos el DAO que ya tienes creado
            planPrecioDAO.insertar(nuevoPlan);
            System.out.println("✅ Plan de precios guardado con éxito.");
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar en la base de datos: " + e.getMessage());
        }
    }
    private static void menuNuevaActividadConfigurada() {
        try {
            System.out.println("\n--- CONFIGURACIÓN DE HORARIO ---");
            ActividadConfigurada ac = new ActividadConfigurada();

            // 1. Datos básicos
            System.out.print("Nombre de la clase (ej. Yoga Lunes mañana): ");
            ac.setNombreClase(teclado.nextLine());

            System.out.print("Día (1-Lunes, 7-Domingo): ");
            ac.setDiaSemana(leerEntero());

            ac.setHoraInicio(leerHora());

            System.out.print("Duración (min): ");
            ac.setDuracion(leerEntero());

            // 2. Selección de Recursos (IDs)
            System.out.println("\n--- Selecciona los recursos ---");
            listarTiposActividad();
            System.out.print("ID del Tipo de Actividad: ");
            ac.setIdTipoActividad(leerEntero());

            listarSalas();
            System.out.print("ID de la Sala: ");
            ac.setIdSala(leerEntero());

            listarProfesores();
            System.out.print("ID del Profesor: ");
            ac.setIdProfesorFijo(leerEntero());

            // Validación de Solapamiento
            // Comprobamos si la SALA está libre en ese rango
            boolean conflictoSala = horarioDAO.existeSolapamiento(
                    ac.getDiaSemana(),
                    ac.getHoraInicio(),
                    ac.getDuracion(),
                    ac.getIdSala()
            );
            // Comprobamos si la PROFESOR está libre en ese rango

            boolean conflictoProfesor = horarioDAO.existeSolapamientoProfesor(
                    ac.getDiaSemana(), ac.getHoraInicio(), ac.getDuracion(), ac.getIdProfesorFijo()
            );

            if (conflictoSala) {
                System.err.println("❌ ERROR: La SALA ya está ocupada en ese horario.");
            } else if (conflictoProfesor) {
                System.err.println("❌ ERROR: El PROFESOR ya tiene otra clase asignada en ese horario.");
            } else {
                // Si todo está libre, guardamos
                horarioDAO.insertar(ac);
                System.out.println("✅ Horario guardado correctamente y sin conflictos.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
        }
    }
    // --- MÉTODOS DE CONSULTA (LISTADOS) ---

    private static void listarUsuariosYPlanes() {
        System.out.println("\n--- ESTADO GLOBAL DE SOCIOS Y SUSCRIPCIONES ---");
        try {
            // Asumiendo que tienes una instancia de usuarioDAO
            List<UsuarioSuscripcionDTO> socios = usuarioDAO.listarUsuariosConPlan();

            if (socios.isEmpty()) {
                System.out.println("⚠️ No hay usuarios registrados.");
                return;
            }

            System.out.printf("%-3s | %-20s | %-25s | %-12s | %-10s | %-8s%n",
                    "ID", "NOMBRE", "EMAIL", "PLAN", "CADUCA", "ESTADO");
            System.out.println("-".repeat(85));

            for (UsuarioSuscripcionDTO s : socios) {
                String fechaStr = (s.getFechaFin() != null) ? s.getFechaFin().toString() : "----";
                System.out.printf("%-3d | %-20s | %-25s | %-12s | %-10s | %-8s%n",
                        s.getIdUsuario(), s.getNombreUsuario(), s.getEmail(),
                        s.getNombrePlan(), fechaStr, s.getEstadoSuscripcion());
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al consultar socios: " + e.getMessage());
        }
    }
    // Este es el método que le falta a tu clase de Menú
    private static void listarPlanesPrecios() {
        try {
            // Llamamos al DAO para traer la lista de la base de datos
            List<PlanPrecio> planes = planPrecioDAO.listarTodos();

            if (planes.isEmpty()) {
                System.out.println("⚠️ No hay planes configurados. Crea uno primero en el menú de altas.");
                return;
            }

            System.out.println("\n--- PLANES DE PRECIOS DISPONIBLES ---");
            System.out.printf("%-3s | %-20s | %-10s | %-10s%n", "ID", "NOMBRE", "PRECIO", "LÍMITE");
            System.out.println("-".repeat(55));

            for (PlanPrecio p : planes) {
                System.out.printf("%-3d | %-20s | %-10.2f | %-10d%n",
                        p.getId(), p.getNombrePlan(), p.getPrecioMensual(), p.getLimiteActividades());
            }
            System.out.println(""); // Espacio extra para que se vea limpio
        } catch (SQLException e) {
            System.err.println("❌ Error al recuperar los planes: " + e.getMessage());
        }
    }
    private static void listarProfesores() {
        System.out.println("\n--- PROFESORES ---");
        try {
            profesorDAO.listar().forEach(p ->
                    System.out.printf("ID: %d | %-20s | %s%n", p.getId(), p.getNombre(), p.getEspecialidad()));
        } catch (Exception e) { System.err.println("❌ Error: " + e.getMessage()); }
    }

    private static void listarSalas() {
        System.out.println("\n--- SALAS ---");
        try {
            salaDAO.listar().forEach(s ->
                    System.out.printf("ID: %d | %-15s | Capacidad: %d | Tipo: %s%n", s.getId(), s.getNombre(), s.getCapacidadMaxima(), s.getTipo()));
        } catch (Exception e) { System.err.println("❌ Error: " + e.getMessage()); }
    }

    private static void listarTiposActividad() {
        System.out.println("\n--- TIPOS DE ACTIVIDAD ---");
        try {
            tipoDAO.listar().forEach(t ->
                    System.out.printf("ID: %d | %-15s | %s%n", t.getId(), t.getNombre(), t.getDescripcion()));
        } catch (Exception e) { System.err.println("❌ Error: " + e.getMessage()); }
    }

    private static void listarHorario() {
        System.out.println("\n--- HORARIO SEMANAL DETALLADO ---");
        try {
            List<HorarioDetalleDTO> horario = horarioDAO.listarHorarioDetallado();
            if (horario.isEmpty()) {
                System.out.println("⚠️ No hay actividades configuradas.");
                return;
            }
            System.out.printf("%-3s | %-25s | %-4s | %-5s | %-5s | %-16s | %-15s%n",
                    "ID", "CLASE", "DÍA", "HORA", "DUR.", "SALA", "PROFESOR");
            System.out.println("-".repeat(90));

            for (HorarioDetalleDTO h : horario) {
                System.out.printf("%-3d | %-25s | %-4s | %-5s | %-5d | %-16s | %-15s%n",
                        h.getId(), h.getNombreClase(), obtenerNombreDia(h.getDiaSemana()),
                        h.getHoraInicio(), h.getDuracion(), h.getNombreSala(), h.getNombreProfesor());
            }
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void mostrarListadoSesiones() {
        try {
            List<SesionDetalleDTO> sesiones = sesionDAO.listarSesionesDetalladas();
            if (sesiones.isEmpty()) {
                System.out.println("⚠️ No hay sesiones programadas.");
                return;
            }
            System.out.println("\n--- ESTADO DE OCUPACIÓN DE SESIONES ---");
            System.out.printf("%-4s | %-20s | %-10s | %-8s | %-15s | %-15s | %-6s | %-6s%n",
                    "ID", "CLASE", "FECHA", "HORA", "SALA", "PROFESOR", "AFORO", "LIBRES");
            System.out.println("-".repeat(110));

            for (SesionDetalleDTO s : sesiones) {
                System.out.printf("%-4d | %-20s | %-10s | %-8s | %-15s | %-15s | %-6d | %-6d%n",
                        s.getIdSesion(), s.getNombreClase(), s.getFecha(),
                        s.getHoraInicio(), s.getNombreSala(), s.getNombreProfesor(),
                        s.getAforoMaximo(), s.getPlazasLibres());
            }
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void listarReservasSocio() {
        System.out.print("\nIntroduce el ID del socio para ver sus reservas: ");
        int idSocio = leerEntero();

        try {
            // ¿Existe el socio?
            Usuario socio = usuarioDAO.buscarPorId(idSocio);

            if (socio == null) {
                System.out.println("❌ Error: El socio con ID " + idSocio + " no existe en el sistema.");
                return; // Salimos del método
            }
            List<ReservaDetalleDTO> reservas = sesionDAO.listarReservasPorUsuario(idSocio);

            if (reservas.isEmpty()) {
                System.out.println("ℹ️ Este socio no tiene reservas registradas.");
                return;
            }

            System.out.println("\n--- RESERVAS DEL SOCIO: " + reservas.get(0).getNombreCliente() + " ---");
            System.out.printf("%-4s | %-20s | %-10s | %-8s | %-12s | %-10s%n",
                    "ID", "ACTIVIDAD", "FECHA", "HORA", "SALA", "ESTADO");
            System.out.println("-".repeat(75));

            for (ReservaDetalleDTO r : reservas) {
                System.out.printf("%-4d | %-20s | %-10s | %-8s | %-12s | %-10s%n",
                        r.getIdReserva(), r.getNombreActividad(), r.getFechaSesion(),
                        r.getHoraSesion(), r.getNombreSala(), r.getEstadoReserva());
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al consultar reservas: " + e.getMessage());
        }
    }
    private static void menuGenerarSesionesMes() {
        System.out.println("\n--- MOTOR DE GENERACIÓN DE SESIONES ---");
        System.out.print("Mes (1-12): ");
        int mes = leerEntero();
        System.out.print("Año (ej. 2026): ");
        int anio = leerEntero();

        try {
            System.out.println("Generando... (evitando duplicados)");
            int creadas = sesionDAO.generarSesionesMensuales(mes, anio);
            System.out.println("✅ Operación finalizada. Creadas: " + creadas);
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void menuGestionSesiones() {
        System.out.println("\n--- GESTIÓN DIARIA DE SESIONES ---");
        mostrarListadoSesiones(); // Para ver los IDs

        System.out.print("\nIntroduce el ID de la sesión a modificar: ");
        int idSesion = leerEntero();

        System.out.println("1. Cancelar sesión");
        System.out.println("2. Asignar/Cambiar profesor sustituto");
        System.out.println("0. Volver");
        System.out.print("Elige una opción: ");

        int opt = leerEntero();
        try {
            switch (opt) {
                case 1 -> {
                    sesionDAO.actualizarEstadoSesion(idSesion, "CANCELADA");
                    System.out.println("✅ Sesión cancelada correctamente.");
                }
                case 2 -> {
                    listarProfesores(); // Para ver IDs de profesores
                    System.out.print("ID del nuevo profesor: ");
                    int idProf = leerEntero();
                    sesionDAO.asignarProfesorSustituto(idSesion, idProf);
                    System.out.println("✅ Profesor sustituto asignado.");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al modificar sesión: " + e.getMessage());
        }
    }
    private static String obtenerNombreDia(int dia) {
        return switch (dia) {
            case 1 -> "LUN"; case 2 -> "MAR"; case 3 -> "MIE";
            case 4 -> "JUE"; case 5 -> "VIE"; case 6 -> "SAB";
            case 7 -> "DOM"; default -> "???";
        };
    }

    private static int leerEntero() {
        while (true) {
            String entrada = teclado.nextLine().trim(); // Leemos como String para controlar el vacío

            if (entrada.isEmpty()) {
                System.out.print("⚠️ No puedes dejarlo vacío. Introduce un número (o '0' para cancelar/salir): ");
                continue;
            }

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.print("❌ Entrada no válida. Por favor, introduce un número entero: ");
            }
        }
    }
    private static double leerDouble() {
        while (true) {
            String entrada = teclado.nextLine().trim();
            if (entrada.isEmpty()) {
                System.out.print("⚠️ No puede estar vacío. Introduce el precio: ");
                continue;
            }
            try {
                return Double.parseDouble(entrada.replace(',', '.')); // Acepta comas y puntos
            } catch (NumberFormatException e) {
                System.out.print("❌ Precio no válido. Usa el formato 00.00: ");
            }
        }
    }
    private static LocalTime leerHora() {
        while (true) {
            System.out.print("Introduce hora (HH:MM): ");
            String entrada = teclado.nextLine().trim();
            if (entrada.isEmpty()) {
                System.out.println("⚠️ La hora no puede estar vacía.");
                continue;
            }
            try {
                return LocalTime.parse(entrada);
            } catch (Exception e) {
                System.out.println("❌ Formato incorrecto. Usa HH:MM (ejemplo: 10:30 o 22:00)");
            }
        }
    }
}