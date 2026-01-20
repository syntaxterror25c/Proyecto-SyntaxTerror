package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.dao.SesionDAO;
import es.cifpcarlos3.model.Sesion;
import es.cifpcarlos3.model.SesionDetalleDTO;
import es.cifpcarlos3.model.ReservaDetalleDTO;
import es.cifpcarlos3.model.enums.EstadoSesion; // Importamos tu Enum

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SesionDAOImpl implements SesionDAO {

    @Override
    public int crearSesion(Connection conn, LocalDate fecha, int idConfig, int idSala, Integer aforoEspec) throws SQLException {
        String sql = "INSERT INTO gym.sesiones (fecha, id_actividad_configurada, id_sala, aforo_especifico, estado) " +
                "SELECT ?, ?, ?, ?, 'ACTIVA' " +
                "WHERE NOT EXISTS (" +
                "    SELECT 1 FROM gym.sesiones " +
                "    WHERE fecha = ? AND id_actividad_configurada = ?" +
                ")";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setInt(2, idConfig);
            ps.setInt(3, idSala);
            if (aforoEspec == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, aforoEspec);
            }
            ps.setDate(5, Date.valueOf(fecha));
            ps.setInt(6, idConfig);

            return ps.executeUpdate();
        }
    }

    public int generarSesionesMensuales(int mes, int anio) throws SQLException {
        int totalInsertadas = 0;
        String sqlHorario = "SELECT id, dia_semana, id_sala, aforo_especifico_plantilla FROM gym.actividad_configurada";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psH = conn.prepareStatement(sqlHorario);
                     ResultSet rs = psH.executeQuery()) {

                    while (rs.next()) {
                        int idConfig = rs.getInt("id");
                        int diaSemanaConfig = rs.getInt("dia_semana");
                        int idSala = rs.getInt("id_sala");
                        Integer aforoPlanti = (Integer) rs.getObject("aforo_especifico_plantilla");

                        LocalDate fecha = LocalDate.of(anio, mes, 1);
                        while (fecha.getMonthValue() == mes) {
                            if (fecha.getDayOfWeek().getValue() == diaSemanaConfig) {
                                totalInsertadas += crearSesion(conn, fecha, idConfig, idSala, aforoPlanti);
                            }
                            fecha = fecha.plusDays(1);
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        return totalInsertadas;
    }

    // --- RECUPERADO: MÉTODO DE ACTUALIZACIÓN DE ESTADO ---
    public void actualizarEstadoSesion(int idSesion, String nuevoEstado) throws SQLException {
        String sqlSesion = "UPDATE gym.sesiones SET estado = ? WHERE id = ?";
        String sqlReservas = "UPDATE gym.reservas SET estado = 'CANCELADA' WHERE id_sesion = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psS = conn.prepareStatement(sqlSesion)) {
                    psS.setString(1, nuevoEstado);
                    psS.setInt(2, idSesion);
                    psS.executeUpdate();
                }

                if ("CANCELADA".equalsIgnoreCase(nuevoEstado)) {
                    try (PreparedStatement psR = conn.prepareStatement(sqlReservas)) {
                        psR.setInt(1, idSesion);
                        psR.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<Sesion> listarPorFecha(LocalDate fecha) throws SQLException {
        List<Sesion> lista = new ArrayList<>();
        String sql = "SELECT * FROM gym.sesiones WHERE fecha = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sesion s = new Sesion();
                    s.setId(rs.getInt("id"));
                    s.setFecha(rs.getDate("fecha").toLocalDate());
                    s.setIdActividadConfigurada(rs.getInt("id_actividad_configurada"));

                    // CORRECCIÓN PARA EL ENUM
                    String est = rs.getString("estado");
                    if (est != null) s.setEstado(EstadoSesion.valueOf(est.toUpperCase()));

                    lista.add(s);
                }
            }
        }
        return lista;
    }

    public List<SesionDetalleDTO> listarSesionesDetalladas() throws SQLException {
        List<SesionDetalleDTO> lista = new ArrayList<>();
        // Nota: He cambiado 'ta.nombre' por 'ta.descripcion' para que coincida con tu SQL anterior
        String sql = "SELECT s.id, s.id_actividad_configurada, ac.nombre_clase, ta.descripcion, s.fecha, " +
                "ac.hora_inicio, ac.duracion, sa.nombre as sala, p.nombre as profesor_fijo, " +
                "ps.nombre as profesor_sustituto, s.estado, " +
                "COALESCE(s.aforo_especifico, sa.capacidad_maxima) as aforo_total, " +
                "(COALESCE(s.aforo_especifico, sa.capacidad_maxima) - COUNT(r.id)) as disponibles " +
                "FROM gym.sesiones s " +
                "JOIN gym.actividad_configurada ac ON s.id_actividad_configurada = ac.id " +
                "JOIN gym.tipo_actividad ta ON ac.id_tipo_actividad = ta.id " +
                "JOIN gym.salas sa ON s.id_sala = sa.id " +
                "JOIN gym.profesores p ON ac.id_profesor_fijo = p.id " +
                "LEFT JOIN gym.profesores ps ON s.id_profesor_sustituto = ps.id " +
                "LEFT JOIN gym.reservas r ON s.id = r.id_sesion AND r.estado = 'CONFIRMADA' " +
                "GROUP BY s.id, s.id_actividad_configurada, ac.nombre_clase, ta.descripcion, s.fecha, " +
                "ac.hora_inicio, ac.duracion, sa.nombre, p.nombre, ps.nombre, s.estado, s.aforo_especifico, sa.capacidad_maxima " +
                "ORDER BY s.fecha, ac.hora_inicio";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SesionDetalleDTO dto = new SesionDetalleDTO();
                dto.setIdSesion(rs.getInt("id"));
                dto.setNombreClase(rs.getString("nombre_clase"));
                dto.setDescripcion(rs.getString("descripcion"));
                dto.setFecha(rs.getDate("fecha").toLocalDate());
                int min = rs.getInt("hora_inicio");
                dto.setHoraInicio(LocalTime.of(min / 60, min % 60));
                dto.setDuracionMinutos(rs.getInt("duracion"));
                dto.setNombreSala(rs.getString("sala"));
                dto.setEstado(rs.getString("estado"));
                dto.setAforoMaximo(rs.getInt("aforo_total"));
                dto.setPlazasLibres(rs.getInt("disponibles"));
                String sustituto = rs.getString("profesor_sustituto");
                dto.setNombreProfesor(sustituto != null ? sustituto + " (Sustituto)" : rs.getString("profesor_fijo"));
                lista.add(dto);
            }
        }
        return lista;
    }

    public String realizarReserva(int idUsuario, int idSesion) throws SQLException {
        // 1. Buscamos la fecha de la sesión a la que se quiere apuntar
        // 2. Contamos sus reservas en el MISMO MES Y AÑO de esa sesión
        // 3. Verificamos que la suscripción cubra esa fecha
        String sqlValidacion =
                "SELECT " +
                        "  p.limite_sesiones, sub.estado as estado_sub, sub.fecha_fin as fin_sub, " +
                        "  ses.fecha as fecha_sesion, ses.estado as estado_sesion, " +
                        "  (SELECT COUNT(*) FROM gym.reservas r " +
                        "   JOIN gym.sesiones s_aux ON r.id_sesion = s_aux.id " +
                        "   WHERE r.id_usuario = ? AND r.estado = 'CONFIRMADA' " +
                        "   AND EXTRACT(MONTH FROM s_aux.fecha) = EXTRACT(MONTH FROM ses.fecha) " +
                        "   AND EXTRACT(YEAR FROM s_aux.fecha) = EXTRACT(YEAR FROM ses.fecha)) as consumidas_mes_sesion, " +
                        "  (COALESCE(ses.aforo_especifico, sa.capacidad_maxima) - " +
                        "   (SELECT COUNT(*) FROM gym.reservas r2 WHERE r2.id_sesion = ses.id AND r2.estado = 'CONFIRMADA')) as disponibles, " +
                        "  (SELECT COUNT(*) FROM gym.reservas WHERE id_usuario = ? AND id_sesion = ? AND estado = 'CONFIRMADA') as ya_reservado " +
                        "FROM gym.usuarios u " +
                        "JOIN gym.suscripciones sub ON u.id = sub.id_usuario " +
                        "JOIN gym.plan_precios p ON sub.id_plan = p.id " +
                        "JOIN gym.sesiones ses ON ses.id = ? " +
                        "JOIN gym.salas sa ON ses.id_sala = sa.id " +
                        "WHERE u.id = ? AND sub.estado = 'ACTIVA'";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement psVal = conn.prepareStatement(sqlValidacion)) {
                psVal.setInt(1, idUsuario);
                psVal.setInt(2, idUsuario);
                psVal.setInt(3, idSesion);
                psVal.setInt(4, idSesion);
                psVal.setInt(5, idUsuario);

                try (ResultSet rs = psVal.executeQuery()) {
                    if (rs.next()) {
                        java.time.LocalDate finSub = rs.getDate("fin_sub").toLocalDate();
                        java.time.LocalDate fechaSes = rs.getDate("fecha_sesion").toLocalDate();

                        // VALIDACIÓN 1: ¿Suscripción en vigor para la fecha elegida?
                        if (fechaSes.isAfter(finSub)) {
                            return "❌ Error: Tu suscripción caduca el " + finSub + " y no cubre la sesión del " + fechaSes;
                        }

                        // VALIDACIÓN 2: ¿Límite del plan para ESE mes?
                        int limite = rs.getInt("limite_sesiones");
                        int consumidas = rs.getInt("consumidas_mes_sesion");
                        if (limite > 0 && consumidas >= limite) {
                            return "⚠️ Límite de plan alcanzado para el mes de " + fechaSes.getMonth() + ": (" + consumidas + "/" + limite + ")";
                        }

                        // VALIDACIÓN 3: Duplicados
                        if (rs.getInt("ya_reservado") > 0) {
                            return "❌ Ya estás apuntado a esta sesión.";
                        }

                        // VALIDACIÓN 4: Aforo
                        if (rs.getInt("disponibles") <= 0) {
                            return "❌ Sesión completa.";
                        }
                    } else {
                        return "❌ No tienes una suscripción ACTIVA vinculada.";
                    }
                }
            }

            // Si todo OK, insertamos la reserva
            String sqlInsert = "INSERT INTO gym.reservas (id_usuario, id_sesion, fecha_registro, estado) VALUES (?, ?, CURRENT_TIMESTAMP, 'CONFIRMADA')";
            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, idUsuario);
                psInsert.setInt(2, idSesion);
                psInsert.executeUpdate();
                return "✅ Reserva confirmada con éxito.";
            }
        }
    }
    public boolean cancelarReserva(int idReserva) throws SQLException {
        String sql = "UPDATE gym.reservas SET estado = 'CANCELADA' WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReserva);
            int filasAfectadas = ps.executeUpdate();

            return filasAfectadas > 0; // Devuelve true si encontró la reserva y la canceló
        }
    }
    public String verificarSuscripcionVigente(int idUsuario, int idSesion) throws SQLException {
        // Buscamos si existe una suscripción ACTIVA que cubra la fecha de la sesión elegida
        String sql = "SELECT s.estado, s.fecha_fin, ses.fecha as fecha_sesion " +
                "FROM gym.suscripciones s " +
                "JOIN gym.sesiones ses ON ses.id = ? " +
                "WHERE s.id_usuario = ? AND s.estado = 'ACTIVA'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSesion);
            ps.setInt(2, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.time.LocalDate fechaFin = rs.getDate("fecha_fin").toLocalDate();
                    java.time.LocalDate fechaSesion = rs.getDate("fecha_sesion").toLocalDate();

                    // Si la sesión es después de que caduque su suscripción...
                    if (fechaSesion.isAfter(fechaFin)) {
                        return "❌ Error: Tu suscripción caduca el " + fechaFin + " y la sesión es el " + fechaSesion;
                    }
                    return "OK"; // Todo en orden
                } else {
                    return "❌ Error: No tienes una suscripción ACTIVA en el sistema.";
                }
            }
        }
    }

    public void asignarProfesorSustituto(int idSesion, int idProfesor) throws SQLException {
        String sql = "UPDATE gym.sesiones SET id_profesor_sustituto = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProfesor);
            ps.setInt(2, idSesion);
            ps.executeUpdate();
        }
    }

    public List<ReservaDetalleDTO> listarReservasPorUsuario(int idUsuario) throws SQLException {
        // ... (Mantener igual que antes)
        List<ReservaDetalleDTO> lista = new ArrayList<>();
        String sql = "SELECT r.id AS id_reserva, ac.nombre_clase, s.fecha, ac.hora_inicio, sa.nombre AS nombre_sala, r.estado, u.nombre AS nombre_usuario " +
                "FROM gym.reservas r JOIN gym.sesiones s ON r.id_sesion = s.id " +
                "JOIN gym.actividad_configurada ac ON s.id_actividad_configurada = ac.id " +
                "JOIN gym.salas sa ON s.id_sala = sa.id JOIN gym.usuarios u ON r.id_usuario = u.id " +
                "WHERE r.id_usuario = ? ORDER BY s.fecha, ac.hora_inicio";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReservaDetalleDTO dto = new ReservaDetalleDTO();
                    dto.setIdReserva(rs.getInt("id_reserva"));
                    dto.setNombreActividad(rs.getString("nombre_clase"));
                    dto.setFechaSesion(rs.getDate("fecha").toLocalDate());
                    dto.setHoraSesion(LocalTime.of(rs.getInt("hora_inicio") / 60, rs.getInt("hora_inicio") % 60));
                    dto.setNombreSala(rs.getString("nombre_sala"));
                    dto.setEstadoReserva(rs.getString("estado"));
                    dto.setNombreCliente(rs.getString("nombre_usuario"));
                    lista.add(dto);
                }
            }
        }
        return lista;
    }
}