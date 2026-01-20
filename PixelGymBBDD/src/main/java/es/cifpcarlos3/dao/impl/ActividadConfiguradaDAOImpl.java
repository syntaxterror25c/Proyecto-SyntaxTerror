package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.ActividadConfigurada;
import es.cifpcarlos3.model.HorarioDetalleDTO;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ActividadConfiguradaDAOImpl {

    // 1. INSERTAR (Usando minutos enteros)
    public void insertar(ActividadConfigurada ac) throws SQLException {
        // Añadimos el octavo campo: aforo_especifico_plantilla
        String sql = "INSERT INTO gym.actividad_configurada " +
                "(nombre_clase, dia_semana, hora_inicio, duracion, id_tipo_actividad, id_sala, id_profesor_fijo, aforo_especifico_plantilla) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ac.getNombreClase());
            ps.setInt(2, ac.getDiaSemana());
            ps.setInt(3, ac.getHoraInicioEnMinutos());
            ps.setInt(4, ac.getDuracion());
            ps.setInt(5, ac.getIdTipoActividad());
            ps.setInt(6, ac.getIdSala());
            ps.setInt(7, ac.getIdProfesorFijo());

            // Manejamos el Integer (que puede ser null)
            if (ac.getAforoEspecifico() == null) {
                ps.setNull(8, java.sql.Types.INTEGER);
            } else {
                ps.setInt(8, ac.getAforoEspecifico());
            }

            ps.executeUpdate();
        }
    }

    // 2. LISTAR COMPLETO (Para lógica interna)
    public List<ActividadConfigurada> listarHorarioCompleto() throws SQLException {
        List<ActividadConfigurada> lista = new ArrayList<>();
        String sql = "SELECT * FROM gym.actividad_configurada ORDER BY dia_semana, hora_inicio";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ActividadConfigurada ac = new ActividadConfigurada();
                ac.setId(rs.getInt("id"));
                ac.setNombreClase(rs.getString("nombre_clase"));
                ac.setDiaSemana(rs.getInt("dia_semana"));

                // IMPORTANTE: Convierte el int de la BD al LocalTime del objeto
                ac.setHoraInicioDesdeMinutos(rs.getInt("hora_inicio"));

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
                "t.nombre as nombre_tipo, sa.nombre as nombre_sala, p.nombre as nombre_profesor, " +
                "ac.aforo_especifico_plantilla " +
                "FROM gym.actividad_configurada ac " +
                "JOIN gym.tipo_actividad t ON ac.id_tipo_actividad = t.id " +
                "JOIN gym.salas sa ON ac.id_sala = sa.id " +
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
                // Convertimos minutos a LocalTime
                int minutos = rs.getInt("hora_inicio");
                dto.setHoraInicio(LocalTime.of(minutos / 60, minutos % 60));
                dto.setDuracion(rs.getInt("duracion"));
                dto.setNombreTipo(rs.getString("nombre_tipo"));
                dto.setNombreSala(rs.getString("nombre_sala"));
                dto.setNombreProfesor(rs.getString("nombre_profesor"));
                // Usamos el nombre exacto de tu atributo
                dto.setAforoEspecifico((Integer) rs.getObject("aforo_especifico_plantilla"));

                lista.add(dto);
            }
        }
        return lista;
    }

    // 4. SOLAPAMIENTO SALA
    public boolean existeSolapamiento(int diaSemana, LocalTime inicio, int duracion, int idSala) throws SQLException {
        int inicioMin = (inicio.getHour() * 60) + inicio.getMinute();
        int finMin = inicioMin + duracion;

        // Buscamos si hay alguna clase donde:
        // El inicio de la nueva sea menor que el fin de la existente
        // Y el fin de la nueva sea mayor que el inicio de la existente
        String sql = "SELECT COUNT(*) FROM gym.actividad_configurada " +
                "WHERE dia_semana = ? AND id_sala = ? " +
                "AND (? < (hora_inicio + duracion)) " +
                "AND (? > hora_inicio)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diaSemana);
            ps.setInt(2, idSala);
            ps.setInt(3, inicioMin);
            ps.setInt(4, finMin);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // 5. SOLAPAMIENTO PROFESOR
    public boolean existeSolapamientoProfesor(int diaSemana, LocalTime inicio, int duracion, int idProfesor) throws SQLException {
        int inicioMin = (inicio.getHour() * 60) + inicio.getMinute();
        String sql = "SELECT COUNT(*) FROM gym.actividad_configurada " +
                "WHERE dia_semana = ? AND id_profesor_fijo = ? " +
                "AND (? < (hora_inicio + duracion)) " +
                "AND ((? + ?) > hora_inicio)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diaSemana);
            ps.setInt(2, idProfesor);
            ps.setInt(3, inicioMin);
            ps.setInt(4, inicioMin);
            ps.setInt(5, duracion);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}