package es.cifpcarlos3.ui;

import es.cifpcarlos3.dao.impl.*;
import es.cifpcarlos3.model.*;

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
            System.out.println("1. CONSULTAS Y LISTADOS (Ver qué hay)");
            System.out.println("2. GESTIÓN DE RECURSOS (Altas de Usuarios, Profesores, Salas, Tipos)");
            System.out.println("3. CONFIGURACIÓN DE HORARIOS (Plantillas)");
            System.out.println("4. GENERAR SESIONES DEL MES (Motor)");
            System.out.println("5. GESTIÓN DIARIA (Cancelar sesión/Sustituciones)");
            System.out.println("6. Reservar sesión");
            System.out.println("7. Ver reservas de un socio");
            System.out.println("8. Probar Login de Socio");
            System.out.println("9. Cancelar una Reserva específica");
            System.out.println("0. SALIR");
            System.out.print("Elige una categoría: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1 -> menuListados();
                case 2 -> menuGestionRecursos();
                case 3 -> menuNuevaActividadConfigurada();
                case 4 -> menuGenerarSesionesMes();
                case 5 -> menuGestionSesiones();
                case 6 -> procesarReserva();
                case 7 -> listarReservasSocio();
                case 8 -> procesarLogin();
                case 9 -> procesarCancelacionReserva();
                case 0 -> System.out.println("¡Hasta pronto!");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }


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
        System.out.println("1. Listado de socios (Usuarios + Planes)");
        System.out.println("2. Listado de Profesores");
        System.out.println("3. Listado de Salas");
        System.out.println("4. Listado de Tipos de Actividad");
        System.out.println("5. Horarios semanal (Plantilla Detallada)");
        System.out.println("6. Sesiones calendario (Ocupación Real)");
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
        nuevoPlan.setLimiteSesiones(leerEntero());

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
            System.out.print("Nombre de la clase (ej. Yoga Lunes 22:00): ");
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

            System.out.print("➤ Aforo específico (Enter para usar el de la sala): ");
            String inputAforo = teclado.nextLine().trim();
            if (!inputAforo.isEmpty()) {
                try {
                    ac.setAforoEspecifico(Integer.parseInt(inputAforo));
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Formato no válido, se usará el de la sala.");
                }
            }
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
            List<PlanPrecio> planes = planPrecioDAO.listarTodos();

            if (planes.isEmpty()) {
                System.out.println("⚠️ No hay planes configurados. Crea uno primero en el menú de altas.");
                return;
            }

            System.out.println("\n--- PLANES DE PRECIOS DISPONIBLES ---");
            System.out.printf("%-3s | %-20s | %-10s | %-10s%n", "ID", "NOMBRE", "PRECIO", "LÍMITE");
            System.out.println("-".repeat(55));

            for (PlanPrecio p : planes) {
                // CORRECCIÓN AQUÍ: Era getLimiteSesiones()
                System.out.printf("%-3d | %-20s | %-10.2f | %-10d%n",
                        p.getId(), p.getNombrePlan(), p.getPrecioMensual(), p.getLimiteSesiones());
            }
            System.out.println("");
        } catch (SQLException e) {
            System.err.println("❌ Error al recuperar los planes: " + e.getMessage());
        }
    }
    private static void listarProfesores() {
        System.out.println("\n--- PROFESORES ---");
        try {
            profesorDAO.listarProfesores().forEach(p ->
                    System.out.printf("ID: %d | Nombre: %-20s | Especialidad: %-20s | Tel: %s%n",
                            p.getId(), p.getNombre(), p.getEspecialidad(), p.getTelefono()));
        } catch (Exception e) { System.err.println("❌ Error: " + e.getMessage()); }
    }

    private static void listarSalas() {
        System.out.println("\n--- LISTADO GLOBAL DE SALAS ---");
        try {
            List<Sala> salas = salaDAO.listar();

            if (salas.isEmpty()) {
                System.out.println("No hay salas registradas actualmente.");
                return;
            }

            // Cabecera formateada para alineación perfecta
            System.out.printf("%-5s | %-20s | %-10s%n", "ID", "NOMBRE SALA", "CAPACIDAD");
            System.out.println("---------------------------------------------------------");

            // Usamos el forEach pero con un printf que añade el salto de línea (%n) al final
            salas.forEach(s ->
                    System.out.printf("%-5d | %-20s | %-10d%n",
                            s.getId(),
                            s.getNombre(),
                            s.getCapacidadMaxima())
            );

            System.out.println("---------------------------------------------------------");

        } catch (Exception e) {
            System.err.println("❌ Error al consultar salas: " + e.getMessage());
        }
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
            // Usamos el método que ya tienes en el DAO
            List<HorarioDetalleDTO> horario = horarioDAO.listarHorarioDetallado();
            if (horario.isEmpty()) {
                System.out.println("⚠️ No hay actividades configuradas.");
                return;
            }

            // Ajustamos la cabecera para que quepa la nueva columna de AFORO
            System.out.printf("%-3s | %-25s | %-4s | %-5s | %-5s | %-16s | %-20s | %-6s%n",
                    "ID", "CLASE", "DÍA", "HORA", "DUR.", "SALA", "PROFESOR", "AFORO");
            System.out.println("-".repeat(110));

            for (HorarioDetalleDTO h : horario) {
                // Si el aforo es null en la plantilla, mostramos "SALA"
                String aforoTxt = (h.getAforoEspecifico() != null) ? String.valueOf(h.getAforoEspecifico()) : "SALA";

                System.out.printf("%-3d | %-25s | %-4s | %-5s | %-5d | %-16s | %-20s | %-6s%n",
                        h.getId(),
                        h.getNombreClase(),
                        obtenerNombreDia(h.getDiaSemana()),
                        h.getHoraInicio(),
                        h.getDuracion(),
                        h.getNombreSala(),
                        h.getNombreProfesor(),
                        aforoTxt);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void mostrarListadoSesiones() {
        try {
            List<SesionDetalleDTO> sesiones = sesionDAO.listarSesionesDetalladas();

            if (sesiones.isEmpty()) {
                System.out.println("\n⚠️ No hay sesiones programadas en el sistema.");
                return;
            }

            System.out.println("\n" + "=".repeat(135));
            System.out.println("                                      CALENDARIO DE SESIONES Y OCUPACIÓN REAL");
            System.out.println("=".repeat(135));

            // Cabecera con los nuevos campos
            System.out.printf("%-4s | %-20s | %-10s | %-8s | %-7s | %-20s | %-20s | %-6s | %-6s | %-15s%n",
                    "ID", "CLASE", "FECHA", "HORA", "DUR.", "SALA", "PROFESOR", "AFORO", "LIBRES", "ESTADO");
            System.out.println("-".repeat(135));

            for (SesionDetalleDTO s : sesiones) {
                // Formateamos el estado para que sea visualmente claro
                String estadoVisual = s.getEstado().equalsIgnoreCase("ACTIVA") ? "🟢 ACTIVA" : "🔴 CANCELADA";

                System.out.printf("%-4d | %-20s | %-10s | %-8s | %-7s | %-20s | %-20s | %-6d | %-6d | %-15s%n",
                        s.getIdSesion(),
                        truncarTexto(s.getNombreClase()),
                        s.getFecha(),
                        (s.getHoraInicio() != null ? s.getHoraInicio() : "--:--"),                        s.getDuracionMinutos() + "'", // <--- Montamos el String aquí
                        truncarTexto(s.getNombreSala()),
                        truncarTexto(s.getNombreProfesor()),
                        s.getAforoMaximo(),
                        s.getPlazasLibres(),
                        estadoVisual);

                // Imprimimos la descripción debajo en una línea secundaria para no ensanchar la tabla
                System.out.println("     └─ Desc: " + (s.getDescripcion() != null ? s.getDescripcion() : "Sin descripción disponible."));                System.out.println("-".repeat(135));
            }
        } catch (Exception e) {
            System.err.println("❌ Error al recuperar el listado: " + e.getMessage());
        }
    }

    // Función auxiliar para que el texto no rompa las columnas si es muy largo
    private static String truncarTexto(String texto) {
        if (texto == null) return "";
        return (texto.length() > 20) ? texto.substring(0, 20 - 3) + "..." : texto;
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

            System.out.println("\n--- RESERVAS DEL SOCIO: " + reservas.getFirst().getNombreCliente() + " ---");
            System.out.printf("%-4s | %-25s | %-10s | %-8s | %-18s | %-10s%n",
                    "ID", "ACTIVIDAD", "FECHA", "HORA", "SALA", "ESTADO");
            System.out.println("-".repeat(75));

            for (ReservaDetalleDTO r : reservas) {
                System.out.printf("%-4d | %-25s | %-10s | %-8s | %-18s | %-10s%n",
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
    private static void procesarReserva() {
        try {
            // 1. Cargar las sesiones disponibles
            List<SesionDetalleDTO> sesiones = sesionDAO.listarSesionesDetalladas();

            // VALIDACIÓN A: ¿Hay sesiones en el sistema?
            if (sesiones.isEmpty()) {
                System.out.println("\n⚠️ No hay sesiones generadas. Ve primero al paso 4 (Motor).");
                return;
            }

            // 2. Mostrar la información al usuario
            listarUsuariosYPlanes();
            mostrarListadoSesiones();

            // 3. Recogida de datos
            System.out.println("\n>>> FORMULARIO DE RESERVA");
            System.out.print("➤ ID del Usuario: ");
            int idU = leerEntero();

            System.out.print("➤ ID de la Sesión: ");
            int idS = leerEntero();

            // --- NUEVA VALIDACIÓN DE SUSCRIPCIÓN (SEGURIDAD) ---
            // Comprobamos si el socio tiene plan activo y si no ha caducado para esa sesión
            String validacionSuscripcion = sesionDAO.verificarSuscripcionVigente(idU, idS);
            if (!validacionSuscripcion.equals("OK")) {
                System.err.println("\n" + validacionSuscripcion);
                return; // Bloqueamos la reserva si no hay suscripción válida
            }

            // VALIDACIÓN B: ¿Existe la sesión y tiene plazas?
            SesionDetalleDTO sesionElegida = null;
            for (SesionDetalleDTO s : sesiones) {
                if (s.getIdSesion() == idS) {
                    sesionElegida = s;
                    break;
                }
            }

            if (sesionElegida == null) {
                System.out.println("❌ Error: La sesión con ID " + idS + " no existe.");
                return;
            }

            if (sesionElegida.getPlazasLibres() <= 0) {
                System.out.println("❌ Error: La sesión '" + sesionElegida.getNombreClase() + "' está llena.");
                return;
            }

            // 4. Si todo es correcto, llamamos al DAO para ejecutar la transacción
            System.out.println("⏳ Procesando reserva...");
            String resultado = sesionDAO.realizarReserva(idU, idS);
            System.out.println("\n" + resultado);

        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
        }
    }

    private static void procesarLogin() {
        System.out.println("\n--- PRUEBA DE LOGIN DE SOCIO ---");
        System.out.print("Introduce Email: ");
        String email = teclado.nextLine();
        System.out.print("Introduce Contraseña: ");
        String pass = teclado.nextLine();

        try {
            Usuario u = usuarioDAO.validarLogin(email, pass);
            if (u != null) {
                System.out.println("✅ Login correcto. ¡Bienvenido, " + u.getNombre() + "! (ID: " + u.getId() + ")");
            } else {
                System.out.println("❌ Credenciales incorrectas.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error en el login: " + e.getMessage());
        }
    }

    private static void procesarCancelacionReserva() {
        System.out.println("\n--- CANCELACIÓN DE RESERVA ---");
        System.out.print("Introduce el ID de la Reserva que deseas cancelar: ");
        int idReserva = leerEntero();

        try {
            boolean exito = sesionDAO.cancelarReserva(idReserva);
            if (exito) {
                System.out.println("✅ Reserva cancelada correctamente. La plaza ha sido liberada.");
            } else {
                System.out.println("❌ No se encontró ninguna reserva con ese ID.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cancelar: " + e.getMessage());
        }
    }
}