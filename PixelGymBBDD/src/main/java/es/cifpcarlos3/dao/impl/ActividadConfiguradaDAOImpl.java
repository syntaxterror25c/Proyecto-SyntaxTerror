package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.ActividadConfigurada;
import es.cifpcarlos3.model.HorarioDetalleDTO;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ActividadConfiguradaDAOImpl {

    public void insertar(ActividadConfigurada ac) throws SQLException {
        // Añadimos 'gym.' por seguridad
        String sql = "INSERT INTO gym.actividad_configurada " +
                "(nombre_clase, dia_semana, hora_inicio, duracion, id_tipo_actividad, id_sala, id_profesor_fijo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ac.getNombreClase());
            ps.setInt(2, ac.getDiaSemana());
            ps.setTime(3, Time.valueOf(ac.getHoraInicio()));
            ps.setInt(4, ac.getDuracion());
            ps.setInt(5, ac.getIdTipoActividad());
            ps.setInt(6, ac.getIdSala());
            ps.setInt(7, ac.getIdProfesorFijo());

            ps.executeUpdate();
        }
    }

    public List<ActividadConfigurada> listarHorarioCompleto() throws SQLException {
        List<ActividadConfigurada> lista = new ArrayList<>();
        // Ordenamos por día y luego por hora para que el horario tenga sentido
        String sql = "SELECT * FROM gym.actividad_configurada ORDER BY dia_semana, hora_inicio";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ActividadConfigurada ac = new ActividadConfigurada();
                ac.setId(rs.getInt("id"));
                ac.setNombreClase(rs.getString("nombre_clase"));
                ac.setDiaSemana(rs.getInt("dia_semana"));

                // Conversión segura de SQL Time a Java LocalTime
                Time horaSql = rs.getTime("hora_inicio");
                if (horaSql != null) {
                    ac.setHoraInicio(horaSql.toLocalTime());
                }

                ac.setDuracion(rs.getInt("duracion"));
                ac.setIdTipoActividad(rs.getInt("id_tipo_actividad"));
                ac.setIdSala(rs.getInt("id_sala"));
                ac.setIdProfesorFijo(rs.getInt("id_profesor_fijo"));
                lista.add(ac);
            }
        }
        return lista;
    }
    public List<HorarioDetalleDTO> listarHorarioDetallado() throws SQLException {
        List<HorarioDetalleDTO> lista = new ArrayList<>();
        String sql = "SELECT ac.id, ac.nombre_clase, ac.dia_semana, ac.hora_inicio, ac.duracion, " +
                "ta.nombre as tipo, s.nombre as sala, p.nombre as profesor " +
                "FROM gym.actividad_configurada ac " +
                "JOIN gym.tipo_actividad ta ON ac.id_tipo_actividad = ta.id " +
                "JOIN gym.salas s ON ac.id_sala = s.id " +
                "JOIN gym.profesores p ON ac.id_profesor_fijo = p.id " +
                "ORDER BY ac.dia_semana, ac.hora_inicio";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HorarioDetalleDTO dto = new HorarioDetalleDTO();
                dto.setId(rs.getInt("id"));
                dto.setNombreClase(rs.getString("nombre_clase"));
                dto.setDiaSemana(rs.getInt("dia_semana"));
                dto.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                dto.setDuracion(rs.getInt("duracion"));
                dto.setNombreTipo(rs.getString("tipo"));
                dto.setNombreSala(rs.getString("sala"));
                dto.setNombreProfesor(rs.getString("profesor"));
                lista.add(dto);
            }
        }
        return lista;
    }
    public boolean existeSolapamiento(int diaSemana, LocalTime inicio, int duracionMinutos, int idSala) throws SQLException {
        LocalTime fin = inicio.plusMinutes(duracionMinutos);

        // Esta consulta busca cualquier actividad en el mismo día y sala
        // cuyo rango de tiempo se cruce con el nuevo
        String sql = "SELECT COUNT(*) FROM gym.actividad_configurada " +
                "WHERE dia_semana = ? AND id_sala = ? " +
                "AND (? < (hora_inicio + (duracion || ' minutes')::interval)) " +
                "AND ((? + (? || ' minutes')::interval) > hora_inicio)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, diaSemana);
            ps.setInt(2, idSala);
            ps.setTime(3, java.sql.Time.valueOf(inicio));
            ps.setTime(4, java.sql.Time.valueOf(inicio));
            ps.setInt(5, duracionMinutos);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    public boolean existeSolapamientoProfesor(int diaSemana, LocalTime inicio, int duracionMinutos, int idProfesor) throws SQLException {
        String sql = "SELECT COUNT(*) FROM gym.actividad_configurada " +
                "WHERE dia_semana = ? AND id_profesor_fijo = ? " +
                "AND (? < (hora_inicio + (duracion || ' minutes')::interval)) " +
                "AND ((? + (? || ' minutes')::interval) > hora_inicio)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, diaSemana);
            ps.setInt(2, idProfesor);
            ps.setTime(3, java.sql.Time.valueOf(inicio));
            ps.setTime(4, java.sql.Time.valueOf(inicio));
            ps.setInt(5, duracionMinutos);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}