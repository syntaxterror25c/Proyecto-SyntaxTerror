package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.ActividadConfigurada;
import es.cifpcarlos3.model.Suscripcion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuscripcionDAOImpl {

    public void insertar(Suscripcion s) throws SQLException {
        // El estado se inserta con el cast al ENUM de Postgres
        String sql = "INSERT INTO suscripciones (id_usuario, id_plan, fecha_inicio, fecha_fin, estado) " +
                "VALUES (?, ?, ?, ?, ?::gym.estado_suscripcion)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getIdUsuario());
            ps.setInt(2, s.getIdPlan());
            ps.setDate(3, Date.valueOf(s.getFechaInicio()));
            ps.setDate(4, Date.valueOf(s.getFechaFin()));
            ps.setString(5, s.getEstado().name()); // Envía "ACTIVA", "EXPIRADA", etc.

            ps.executeUpdate();
        }
    }

    public List<Suscripcion> listarPorUsuario(int idUsuario) throws SQLException {
        List<Suscripcion> lista = new ArrayList<>();
        String sql = "SELECT * FROM suscripciones WHERE id_usuario = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Aquí mapearías el ResultSet a tu objeto Suscripcion
                }
            }
        }
        return lista;
    }

    public List<ActividadConfigurada> listarHorarioCompleto() throws SQLException {
        List<ActividadConfigurada> lista = new ArrayList<>();
        // Usamos PreparedStatement incluso para consultas sin parámetros
        String sql = "SELECT * FROM gym.actividad_configurada ORDER BY dia_semana, hora_inicio";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ActividadConfigurada ac = new ActividadConfigurada();
                ac.setId(rs.getInt("id"));
                ac.setNombreClase(rs.getString("nombre_clase"));
                ac.setDiaSemana(rs.getInt("dia_semana"));
                ac.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                ac.setDuracion(rs.getInt("duracion"));
                ac.setIdTipoActividad(rs.getInt("id_tipo_actividad"));
                ac.setIdSala(rs.getInt("id_sala"));
                ac.setIdProfesorFijo(rs.getInt("id_profesor_fijo"));
                lista.add(ac);
            }
        }
        return lista;
    }
}
