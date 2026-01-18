package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.ReservaDetalleDTO;
import es.cifpcarlos3.model.SesionDetalleDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SesionDAOImpl {

    public void crearSesion(Connection conn, LocalDate fecha, int idConfig) throws SQLException {
        String sql = "INSERT INTO gym.sesiones (fecha, id_actividad_configurada, estado) " +
                "VALUES (?, ?, 'ACTIVA'::gym.estado_sesion)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ps.setInt(2, idConfig);
            ps.executeUpdate();
        }
    }

    public List<SesionDetalleDTO> listarSesionesDetalladas() throws SQLException {
        List<SesionDetalleDTO> lista = new ArrayList<>();
        String sql = "SELECT s.id, ac.nombre_clase, s.fecha, ac.hora_inicio, sa.nombre as sala, " +
                "p.nombre as profesor, " +
                "COALESCE(s.aforo_especifico, sa.capacidad_maxima) as aforo_total, " +
                "(COALESCE(s.aforo_especifico, sa.capacidad_maxima) - COUNT(r.id)) as disponibles " +
                "FROM gym.sesiones s " +
                "JOIN gym.actividad_configurada ac ON s.id_actividad_configurada = ac.id " +
                "JOIN gym.salas sa ON ac.id_sala = sa.id " +
                "JOIN gym.profesores p ON ac.id_profesor_fijo = p.id " +
                "LEFT JOIN gym.reservas r ON s.id = r.id_sesion AND r.estado = 'CONFIRMADA' " +
                "GROUP BY s.id, ac.nombre_clase, s.fecha, ac.hora_inicio, sa.nombre, p.nombre, s.aforo_especifico, sa.capacidad_maxima " +
                "ORDER BY s.fecha, ac.hora_inicio";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SesionDetalleDTO dto = new SesionDetalleDTO();
                dto.setIdSesion(rs.getInt("id"));
                dto.setNombreClase(rs.getString("nombre_clase"));
                dto.setFecha(rs.getDate("fecha").toLocalDate());
                dto.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                dto.setNombreSala(rs.getString("sala"));
                dto.setNombreProfesor(rs.getString("profesor"));
                dto.setAforoMaximo(rs.getInt("aforo_total"));
                dto.setPlazasLibres(rs.getInt("disponibles"));
                lista.add(dto);
            }
        }
        return lista;
    }

    public int generarSesionesMensuales(int mes, int anio) throws SQLException {
        int totalInsertadas = 0;
        String sqlHorario = "SELECT id, dia_semana FROM gym.actividad_configurada";
        String sqlCheck = "SELECT COUNT(*) FROM gym.sesiones WHERE fecha = ? AND id_actividad_configurada = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psHorario = conn.prepareStatement(sqlHorario);
                 PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
                 ResultSet rs = psHorario.executeQuery()) {

                while (rs.next()) {
                    int idConfig = rs.getInt("id");
                    int diaBusqueda = rs.getInt("dia_semana");
                    LocalDate fecha = LocalDate.of(anio, mes, 1);
                    while (fecha.getMonthValue() == mes) {
                        if (fecha.getDayOfWeek().getValue() == diaBusqueda) {
                            psCheck.setDate(1, Date.valueOf(fecha));
                            psCheck.setInt(2, idConfig);
                            try (ResultSet rsCheck = psCheck.executeQuery()) {
                                if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                                    crearSesion(conn, fecha, idConfig);
                                    totalInsertadas++;
                                }
                            }
                        }
                        fecha = fecha.plusDays(1);
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

    // MODIFICADO: Ahora cancela también las reservas vinculadas de forma atómica
    public void actualizarEstadoSesion(int idSesion, String nuevoEstado) throws SQLException {
        String sqlSesion = "UPDATE gym.sesiones SET estado = ?::gym.estado_sesion WHERE id = ?";
        String sqlReservas = "UPDATE gym.reservas SET estado = 'CANCELADA' WHERE id_sesion = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psS = conn.prepareStatement(sqlSesion)) {
                    psS.setString(1, nuevoEstado);
                    psS.setInt(2, idSesion);
                    psS.executeUpdate();
                }

                // Si la sesión se cancela, cancelamos sus reservas automáticamente
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
        List<ReservaDetalleDTO> lista = new ArrayList<>();
        String sql = "SELECT r.id, u.nombre as cliente, ac.nombre_clase, s.fecha, ac.hora_inicio, sa.nombre as sala, r.estado " +
                "FROM gym.reservas r " +
                "JOIN gym.usuarios u ON r.id_usuario = u.id " +
                "JOIN gym.sesiones s ON r.id_sesion = s.id " +
                "JOIN gym.actividad_configurada ac ON s.id_actividad_configurada = ac.id " +
                "JOIN gym.salas sa ON ac.id_sala = sa.id " +
                "WHERE u.id = ? " +
                "ORDER BY s.fecha DESC, ac.hora_inicio DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReservaDetalleDTO dto = new ReservaDetalleDTO();
                    dto.setIdReserva(rs.getInt("id"));
                    dto.setNombreCliente(rs.getString("cliente"));
                    dto.setNombreActividad(rs.getString("nombre_clase"));
                    dto.setFechaSesion(rs.getDate("fecha").toLocalDate());
                    dto.setHoraSesion(rs.getTime("hora_inicio").toLocalTime());
                    dto.setNombreSala(rs.getString("sala"));
                    dto.setEstadoReserva(rs.getString("estado"));
                    lista.add(dto);
                }
            }
        }
        return lista;
    }
}