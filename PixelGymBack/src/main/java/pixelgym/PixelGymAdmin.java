package pixelgym;

import pixelgym.config.FirebaseConfig;
import pixelgym.dao.impl.ReservaDAOImpl;
import pixelgym.dao.impl.UsuarioDAOImpl;
import pixelgym.dao.impl.SesionDAOImpl;
import pixelgym.model.Sesion;
import pixelgym.model.Usuario;
import pixelgym.model.Reserva;
import java.util.List;
import java.util.Scanner;
import pixelgym.dao.IUsuarioDAO;
import pixelgym.dao.ISesionDAO;
import pixelgym.dao.IReservaDAO;

import static pixelgym.utils.Utils.truncar;

public class PixelGymAdmin {
    private static final Scanner sc = new Scanner(System.in);

    // NO instanciamos
    private static IUsuarioDAO usuarioDAO;
    private static ISesionDAO sesionDAO;
    private static IReservaDAO reservaDAO;

    public static void main(String[] args) {
        try {
            // inicializamos Firebase
            FirebaseConfig.inicializar();

            // instanciamos los DAOs ahora que la DB ya existe
            usuarioDAO = new UsuarioDAOImpl();
            sesionDAO = new SesionDAOImpl();
            reservaDAO = new ReservaDAOImpl();

            System.out.println(">>> Backend PixelGym conectado correctamente <<<");
            mostrarMenu();
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO: " + e.getMessage());
        }
    }

    public static void mostrarMenu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n========= PANEL DE ADMINISTRACIÓN PIXELGYM =========");
            System.out.println("USUARIOS");
            System.out.println("  11. Ver todos los usuarios");
            System.out.println("  12. Recargar créditos a usuario");
            System.out.println("  13. Informe de créditos totales");
            System.out.println("SESIONES");
            System.out.println("  21. Sesiones por profesor");
            System.out.println("  22. Sesiones por sala");
            System.out.println("  23. Sesiones por fecha");
            System.out.println("  24. Sesiones por actividad");
            System.out.println("  25. Todas las sesiones");
            System.out.println("  26. Generar sesiones del mes (Alta Masiva)");
            System.out.println("RESERVAS");
            System.out.println("  31. Reservas por profesor");
            System.out.println("  32. Reservas por usuario (email)");
            System.out.println("  33. Reservas por fecha");
            System.out.println("  34. Reservas por actividad");
            System.out.println("  35. Todas las reservas");
            System.out.println("FIN");
            System.out.println("   0. Salir");
            System.out.print("\nSelecciona una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
                switch (opcion) {
                    case 11: listarTodosLosUsuarios(); break;
                    case 12: gestionarRecargaCreditos(); break;
                    case 13: mostrarInformeCreditos(); break;
                    case 21: filtrarSesionesPorProfesor(); break;
                    case 22: filtrarSesionesPorSala(); break;
                    case 23: filtrarSesionesPorFecha(); break;
                    case 24: filtrarSesionesPorActividad(); break;
                    case 25: listarTodasLasSesiones(); break;
                    case 26: altaMasivaSesiones(); break;
                    case 31: filtrarReservasPorProfesor(); break;
                    case 32: filtrarReservasPorUsuario(); break;
                    case 33: filtrarReservasPorFecha(); break;
                    case 34: filtrarReservasPorActividad(); break;
                    case 35: listarTodasLasReservas(); break;
                    case 0: System.out.println("Saliendo..."); break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // --- MÉTODOS DE USUARIOS ---

    private static void listarTodosLosUsuarios() {
        System.out.println("\n" + "=============".repeat(7));
        System.out.println(" ".repeat(25) + "PANEL DE CONTROL DE SOCIOS - PIXELGYM");
        System.out.println("=============".repeat(7));

        try {
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();

            System.out.printf("%-18s | %-25s | %-12s | %-12s | %-8s%n",
                    "NOMBRE", "EMAIL", "PLAN", "VENCE", "CRÉD.");
            System.out.println("-".repeat(91));

            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados en la base de datos.");
            } else {
                for (Usuario u : usuarios) {
                    System.out.printf("%-18s | %-25s | %-12s | %-12s | %-8d%n",
                            truncar(u.getNombre_usuario(), 18),
                            truncar(u.getEmail(), 25),
                            u.getNombrePlan().toUpperCase(), // Método nuevo
                            u.getFechaFinPlan(),             // Método nuevo
                            u.getCreditos());                // Tu método de lógica segura
                }
            }
        } catch (Exception e) {
            System.out.println("Error al recuperar los datos de Firebase: " + e.getMessage());
        }
        System.out.println("=============".repeat(7));
    }

    private static void gestionarRecargaCreditos() {
        try {
            System.out.print("Introduce el Email del usuario: ");
            String email = sc.nextLine();
            System.out.print("Introduce la nueva cantidad total de créditos: ");
            long nuevosCreditos = Long.parseLong(sc.nextLine());
            usuarioDAO.actualizarCreditos(email, nuevosCreditos);
            System.out.println("¡Créditos actualizados correctamente!");
        } catch (Exception e) {
            System.out.println("Error en la recarga: " + e.getMessage());
        }
    }

    // --- MÉTODOS DE SESIONES ---

    private static void filtrarSesionesPorProfesor() throws Exception {
        String prof = validarProfesor();
        imprimirTablaSesiones(sesionDAO.buscarPorProfesor(prof), "Sesiones de " + prof);
    }

    private static void filtrarSesionesPorSala() throws Exception {
        String sala = validarSala();
        imprimirTablaSesiones(sesionDAO.buscarPorSala(sala), "Sesiones en " + sala);
    }

    private static void listarTodasLasSesiones() throws Exception {
        imprimirTablaSesiones(sesionDAO.obtenerTodas(), "Todas las Sesiones");
    }

    private static void filtrarSesionesPorFecha() throws Exception {
        String fecha = validarFechaSesion();
        imprimirTablaSesiones(sesionDAO.buscarPorFecha(fecha), "Sesiones del " + fecha);
    }
    private static void filtrarSesionesPorActividad() throws Exception {
        String actividad = validarActividad(); // Usa el validador que ya tenías
        imprimirTablaSesiones(sesionDAO.buscarPorActividad(actividad), "Sesiones de " + actividad);
    }

    // --- MÉTODOS DE RESERVAS ---

    private static void listarTodasLasReservas() throws Exception {
        imprimirTablaReservas(reservaDAO.obtenerTodas(), "Todas las Reservas");
    }

    private static void filtrarReservasPorUsuario() throws Exception {
        System.out.print("Email del usuario: ");
        String email = sc.nextLine();

        // Recibimos un String, no un objeto Usuario
        String uidEncontrado = usuarioDAO.buscarUsuarioPorEmail(email);

        if (uidEncontrado != null) {
            // Buscamos directamente con el String que nos ha devuelto el DAO
            List<Reserva> lista = reservaDAO.buscarPorUid(uidEncontrado);
            imprimirTablaReservas(lista, "Reservas de " + email);
        } else {
            System.out.println("No se encontró ningún usuario con el email: " + email);
        }
    }

    private static void filtrarReservasPorProfesor() throws Exception {
        String prof = validarProfesorReservas();
        List<Reserva> lista = reservaDAO.buscarPorProfesor(prof);
        imprimirTablaReservas(lista, "Reservas del profesor: " + prof);
    }


    private static void filtrarReservasPorActividad() throws Exception {
        String act = validarActividadReservas();
        imprimirTablaReservas(reservaDAO.buscarPorActividad(act), "Reservas de " + act);
    }

    private static void filtrarReservasPorFecha() throws Exception {
        System.out.print("Fecha (DD/MM/AAAA): ");
        String fecha = sc.nextLine();
        imprimirTablaReservas(reservaDAO.buscarPorFecha(fecha), "Reservas del día " + fecha);
    }

    // --- IMPRESORAS ---


    private static void imprimirTablaReservas(List<Reserva> reservas, String titulo) {
        System.out.println("\n" + "=".repeat(105));
        System.out.println(" ".repeat(35) + titulo.toUpperCase());
        System.out.println("=".repeat(105));

        if (reservas.isEmpty()) {
            System.out.println("No hay resultados.");
        } else {
            // Cabecera: EMAIL (25) | ACTIVIDAD (15) | PROFESOR (15) | FECHA (12) | HORA (8) | ESTADO (12)
            System.out.printf("%-25s | %-15s | %-15s | %-12s | %-8s | %-12s%n",
                    "EMAIL USUARIO", "ACTIVIDAD", "PROFESOR", "FECHA", "HORA", "ESTADO");
            System.out.println("-".repeat(105));

            // Ordenar por fecha
            reservas.sort((r1, r2) -> {
                try {
                    // Con formato DD/MM/YYYY
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    java.util.Date d1 = sdf.parse(r1.getFecha_sesion());
                    java.util.Date d2 = sdf.parse(r2.getFecha_sesion());
                    return d1.compareTo(d2); // De más antigua a más reciente
                } catch (Exception e) {
                    return 0; // Si el formato falla, no mueve la posición
                }
            });
            for (Reserva r : reservas) {
                // Lógica para obtener el email a partir del UID
                String emailMostrar = "Desconocido";
                try {
                    Usuario u = usuarioDAO.buscarPorUid(r.getUid());
                    if (u != null && u.getEmail() != null) {
                        emailMostrar = u.getEmail();
                    }
                } catch (Exception e) {
                    emailMostrar = r.getUid(); // Si falla el cruce, mostramos el UID
                }

                System.out.printf("%-25s | %-15s | %-15s | %-12s | %-8s | %-12s%n",
                        truncar(emailMostrar, 25),
                        truncar(r.getNombre_actividad(), 15),
                        truncar(r.getNombre_profesor(), 15),
                        r.getFecha_sesion(),
                        r.getHora_inicio(),
                        r.getEstado_reserva());
            }
        }
        System.out.println("=".repeat(105));
    }
    private static void imprimirTablaSesiones(List<Sesion> sesiones, String titulo) {
        System.out.println("\n" + "=".repeat(95));
        System.out.println(" ".repeat(30) + titulo.toUpperCase());
        System.out.println("=".repeat(95));
        if (sesiones.isEmpty()) {
            System.out.println("No hay resultados.");
        } else {
            // --- AÑADIR ESTE BLOQUE DE ORDENACIÓN ---
            sesiones.sort((s1, s2) -> {
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    java.util.Date d1 = sdf.parse(s1.getFecha());
                    java.util.Date d2 = sdf.parse(s2.getFecha());

                    // Primero comparamos por fecha
                    int comparacionFecha = d1.compareTo(d2);
                    if (comparacionFecha != 0) return comparacionFecha;

                    // Si la fecha es la misma, comparamos por hora para que salgan en orden horario
                    return s1.getHora_inicio().compareTo(s2.getHora_inicio());
                } catch (Exception e) {
                    return 0;
                }
            });
            System.out.printf("%-15s | %-15s | %-20s | %-12s | %-8s | %-10s%n",
                    "ACTIVIDAD", "PROFESOR", "SALA", "FECHA", "HORA", "OCUPACIÓN");
            for (Sesion s : sesiones) {
                System.out.printf("%-15s | %-15s | %-20s | %-12s | %-8s | %-10s%n",
                        truncar(s.getNombre_actividad(), 15), truncar(s.getNombre_profesor(), 15),
                        s.getSala(), s.getFecha(), s.getHora_inicio(), s.getPlazas_ocupadas() + "/" + s.getCapacidad_maxima());
            }
        }
    }

    private static void mostrarInformeCreditos() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println(" ".repeat(10) + "ESTADÍSTICAS DE CRÉDITOS");
        System.out.println("=".repeat(45));

        try {
            long total = usuarioDAO.obtenerSumaTotalCreditos();
            int numUsuarios = usuarioDAO.obtenerTodos().size();

            System.out.printf("║ %-30s | %-8s ║%n", "CONCEPTO", "VALOR");
            System.out.println("-".repeat(45));
            System.out.printf("║ %-30s | %-8d ║%n", "Total Usuarios Activos", numUsuarios);
            System.out.printf("║ %-30s | %-8d ║%n", "Total Créditos en Sistema", total);

            if (numUsuarios > 0) {
                System.out.printf("║ %-30s | %-8.2f ║%n", "Media por Usuario", (double)total/numUsuarios);
            }

        } catch (Exception e) {
            System.out.println("Error al generar el informe: " + e.getMessage());
        }
        System.out.println("=".repeat(45));
    }

    @SuppressWarnings("MagicConstant")
    private static void altaMasivaSesiones() {
        try {
            System.out.println("\n" + "=".repeat(50));
            System.out.println(" ".repeat(10) + "GENERADOR MENSUAL DE SESIONES");
            System.out.println("=".repeat(50));

            String act = validarActividad();
            String prof = validarProfesor();
            String sala = validarSala();

            System.out.print("Hora (HH:mm): "); String hora = sc.nextLine();
            System.out.print("Capacidad Máxima (Aforo): "); int aforo = Integer.parseInt(sc.nextLine());
            System.out.print("Mes (1-12): "); int mes = Integer.parseInt(sc.nextLine());
            System.out.print("Año (AAAA): "); int anio = Integer.parseInt(sc.nextLine());
            System.out.print("Día de la semana (1=Lunes, 7=Dom): "); int diaSemana = Integer.parseInt(sc.nextLine());
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(anio, mes - 1, 1);
            int diasEnMes = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

            int creadas = 0;
            for (int d = 1; d <= diasEnMes; d++) {
                cal.set(anio, mes - 1, d);
                int diaActual = (cal.get(java.util.Calendar.DAY_OF_WEEK) == 1) ? 7 : cal.get(java.util.Calendar.DAY_OF_WEEK) - 1;

                if (diaActual == diaSemana) {
                    String fechaStr = String.format("%02d/%02d/%d", d, mes, anio);
                    String idDoc = act + "_" + fechaStr.replace("/", "-") + "_" + hora.replace(":", "");

                    Sesion s = new Sesion();
                    s.setNombre_actividad(act);
                    s.setNombre_profesor(prof);
                    s.setFecha(fechaStr);
                    s.setHora_inicio(hora);
                    s.setCapacidad_maxima(aforo);
                    s.setPlazas_ocupadas(0);
                    s.setSala(sala);
                    s.setCoste(1);
                    s.setEstado_sesion("ACTIVA");
                    s.setImagen_url("im_rec_" + act.toLowerCase());

                    sesionDAO.crearSesionProgramada(s, idDoc);
                    creadas++;
                }
            }
            System.out.println("\n>>> ÉXITO: Se han generado " + creadas + " sesiones.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String validarActividad() throws Exception {
        while (true) {
            System.out.print("Actividad: ");
            String act = sc.nextLine().trim();
            List<Sesion> todas = sesionDAO.obtenerTodas();
            boolean existe = todas.stream().anyMatch(s -> s.getNombre_actividad().equalsIgnoreCase(act));

            if (existe) return act; // Si existe, devolvemos el nombre y salimos del bucle

            System.out.println("X Error: Actividad no encontrada.");
            System.out.println("-> OPCIONES DISPONIBLES: " + todas.stream().map(Sesion::getNombre_actividad).distinct().toList());
            System.out.println("Por favor, inténtalo de nuevo.\n");
        }
    }

    private static String validarProfesor() throws Exception {
        while (true) {
            System.out.print("Profesor: ");
            String prof = sc.nextLine().trim();
            List<Sesion> todas = sesionDAO.obtenerTodas();
            boolean existe = todas.stream().anyMatch(s -> s.getNombre_profesor().equalsIgnoreCase(prof));

            if (existe) return prof;

            System.out.println("X Error: Profesor no encontrado.");
            System.out.println("-> PROFESORES REGISTRADOS: " + todas.stream().map(Sesion::getNombre_profesor).distinct().toList());
            System.out.println("Por favor, inténtalo de nuevo.\n");
        }
    }

    private static String validarSala() throws Exception {
        while (true) {
            System.out.print("Sala: ");
            String sala = sc.nextLine().trim();
            List<Sesion> todas = sesionDAO.obtenerTodas();
            boolean existe = todas.stream().anyMatch(s -> s.getSala().equalsIgnoreCase(sala));

            if (existe) return sala;

            System.out.println("X Error: Sala no encontrada.");
            System.out.println("-> SALAS DISPONIBLES: " + todas.stream().map(Sesion::getSala).distinct().toList());
            System.out.println("Por favor, inténtalo de nuevo.\n");
        }
    }

    private static String validarFechaSesion() throws Exception {
        while (true) {
            System.out.print("Fecha (DD/MM/AAAA): ");
            String fecha = sc.nextLine().trim();
            List<Sesion> todas = sesionDAO.obtenerTodas();
            boolean existe = todas.stream().anyMatch(s -> s.getFecha().equals(fecha));

            if (existe) return fecha;

            System.out.println("X Error: No hay sesiones programadas para esa fecha.");
            // Mostramos las próximas 5 fechas
            List<String> fechasDisponibles = todas.stream()
                    .map(Sesion::getFecha)
                    .distinct()
                    .limit(5)
                    .toList();
            System.out.println("-> ALGUNAS FECHAS CON SESIONES: " + fechasDisponibles);
            System.out.println("Inténtalo de nuevo.\n");
        }
    }

    private static String validarProfesorReservas() throws Exception {
        while (true) {
            System.out.print("Introduce el nombre del profesor: ");
            String prof = sc.nextLine().trim();
            List<Reserva> todas = reservaDAO.obtenerTodas();
            boolean existe = todas.stream().anyMatch(r -> r.getNombre_profesor().toLowerCase().contains(prof.toLowerCase()));

            if (existe) return prof;

            System.out.println("X Error: No hay reservas con ese profesor.");
            System.out.println("-> PROFESORES CON RESERVAS: " + todas.stream().map(Reserva::getNombre_profesor).distinct().toList());
            System.out.println("Inténtalo de nuevo.\n");
        }
    }

    private static String validarActividadReservas() throws Exception {
        while (true) {
            System.out.print("Actividad: ");
            String act = sc.nextLine().trim();
            List<Reserva> todas = reservaDAO.obtenerTodas();
            boolean existe = todas.stream().anyMatch(r -> r.getNombre_actividad().equalsIgnoreCase(act));

            if (existe) return act;

            System.out.println("X Error: No hay reservas para esa actividad.");
            System.out.println("-> ACTIVIDADES RESERVADAS: " + todas.stream().map(Reserva::getNombre_actividad).distinct().toList());
            System.out.println("Inténtalo de nuevo.\n");
        }
    }
}