package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.Usuario;
import es.cifpcarlos3.model.UsuarioSuscripcionDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl {

    public void registrarUsuarioConPlan(Usuario u, int idPlan) throws SQLException {
        String sqlUser = "INSERT INTO usuarios (nombre, email, password_hash, telefono) VALUES (?, ?, ?, ?) RETURNING id";
        String sqlSuscripcion = "INSERT INTO suscripciones (id_usuario, id_plan, fecha_inicio, fecha_fin, estado) VALUES (?, ?, CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', 'ACTIVA')";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Empezamos la transacción

            try (PreparedStatement psUser = conn.prepareStatement(sqlUser)) {
                psUser.setString(1, u.getNombre());
                psUser.setString(2, u.getEmail());
                psUser.setString(3, u.getPasswordHash());
                psUser.setString(4, u.getTelefono());

                // Ejecutamos y obtenemos el ID generado
                ResultSet rs = psUser.executeQuery();
                if (rs.next()) {
                    int nuevoIdUsuario = rs.getInt(1);

                    // Insertamos la suscripción vinculada
                    try (PreparedStatement psSub = conn.prepareStatement(sqlSuscripcion)) {
                        psSub.setInt(1, nuevoIdUsuario);
                        psSub.setInt(2, idPlan);
                        psSub.executeUpdate();
                    }
                }
                conn.commit(); // Todo bien, guardamos cambios
            } catch (SQLException e) {
                conn.rollback(); // Algo falló, deshacemos todo
                throw e;
            }
        }
    }
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre FROM gym.usuarios WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    return u;
                }
            }
        }
        return null; // Si no lo encuentra, devuelve null
    }
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setEmail(rs.getString("email"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setTelefono(rs.getString("telefono"));
                    u.setFechaAlta(rs.getDate("fecha_alta").toLocalDate());
                    return u;
                }
            }
        }
        return null;
    }
    public List<UsuarioSuscripcionDTO> listarUsuariosConPlan() throws SQLException {
        List<UsuarioSuscripcionDTO> lista = new ArrayList<>();

        // Añadimos "gym." delante de cada tabla para que PostgreSQL las encuentre
        String sql = "SELECT u.id, u.nombre, u.email, p.nombre_plan as plan, s.fecha_fin, s.estado " +
                "FROM gym.usuarios u " +
                "LEFT JOIN gym.suscripciones s ON u.id = s.id_usuario " +
                "LEFT JOIN gym.plan_precios p ON s.id_plan = p.id " +
                "ORDER BY s.estado DESC, u.nombre ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UsuarioSuscripcionDTO dto = new UsuarioSuscripcionDTO();
                dto.setIdUsuario(rs.getInt("id"));
                dto.setNombreUsuario(rs.getString("nombre"));
                dto.setEmail(rs.getString("email"));
                dto.setNombrePlan(rs.getString("plan") != null ? rs.getString("plan") : "SIN PLAN");

                java.sql.Date fecha = rs.getDate("fecha_fin");
                if (fecha != null) dto.setFechaFin(fecha.toLocalDate());

                dto.setEstadoSuscripcion(rs.getString("estado") != null ? rs.getString("estado") : "N/A");
                lista.add(dto);
            }
        }
        return lista;
    }
}