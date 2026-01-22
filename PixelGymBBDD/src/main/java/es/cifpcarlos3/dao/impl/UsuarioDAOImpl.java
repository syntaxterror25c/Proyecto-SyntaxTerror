package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.Usuario;
import es.cifpcarlos3.model.UsuarioSuscripcionDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl {

    // Inserta el id_plan directamente en la tabla usuarios
    public void registrarUsuarioConPlan(Usuario u, int idPlan) throws SQLException {
        String sqlUsuario = "INSERT INTO gym.usuarios (nombre, email, telefono, password_hash, fecha_alta) VALUES (?, ?, ?, ?, CURRENT_DATE) RETURNING id";
        String sqlSuscripcion = "INSERT INTO gym.suscripciones (id_usuario, id_plan, fecha_inicio, fecha_fin, estado) VALUES (?, ?, CURRENT_DATE, ?, 'ACTIVA')";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Iniciamos transacción

            int idGenerado;
            // 1. Insertar Usuario
            try (PreparedStatement psU = conn.prepareStatement(sqlUsuario)) {
                psU.setString(1, u.getNombre());
                psU.setString(2, u.getEmail());
                psU.setString(3, u.getTelefono());
                psU.setString(4, u.getPasswordHash());

                try (ResultSet rs = psU.executeQuery()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                    } else {
                        throw new SQLException("Error al obtener ID de usuario.");
                    }
                }
            }

            // 2. Insertar Suscripción (3 meses exactos)
            try (PreparedStatement psS = conn.prepareStatement(sqlSuscripcion)) {
                psS.setInt(1, idGenerado);
                psS.setInt(2, idPlan);
                // Calculamos la fecha de fin: Hoy + 3 meses
                java.sql.Date fechaFin = java.sql.Date.valueOf(java.time.LocalDate.now().plusMonths(3));
                psS.setDate(3, fechaFin);

                psS.executeUpdate();
            }

            conn.commit(); // Todo OK
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public Usuario validarLogin(String email, String password) throws SQLException {
        String sql = "SELECT * FROM gym.usuarios WHERE email = ? AND password_hash = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setEmail(rs.getString("email"));
                    // No devolvemos el password por seguridad, pero sí su ID y Nombre
                    return u;
                }
            }
        }
        return null; // Si las credenciales son malas, devuelve null
    }
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, id_plan FROM gym.usuarios WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    // Si tu modelo Usuario tiene setIdPlan, podrías setearlo aquí
                    return u;
                }
            }
        }
        return null;
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM gym.usuarios WHERE email = ?";
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

        // Ahora hacemos el JOIN con la tabla suscripciones (s) para traer datos reales
        String sql = "SELECT u.id, u.nombre, u.email, p.nombre_plan, s.fecha_fin, s.estado " +
                "FROM gym.usuarios u " +
                "LEFT JOIN gym.suscripciones s ON u.id = s.id_usuario " +
                "LEFT JOIN gym.planprecios p ON s.id_plan = p.id " +
                "ORDER BY u.id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UsuarioSuscripcionDTO dto = new UsuarioSuscripcionDTO();
                dto.setIdUsuario(rs.getInt("id"));
                dto.setNombreUsuario(rs.getString("nombre"));
                dto.setEmail(rs.getString("email"));

                // Datos del Plan
                dto.setNombrePlan(rs.getString("nombre_plan") != null ? rs.getString("nombre_plan") : "SIN PLAN");

                // Fecha de Caducidad REAL (de la tabla suscripciones)
                Date fechaFin = rs.getDate("fecha_fin");
                if (fechaFin != null) {
                    dto.setFechaFin(fechaFin.toLocalDate());
                }

                // Estado REAL (de la tabla suscripciones)
                String estado = rs.getString("estado");
                dto.setEstadoSuscripcion(estado != null ? estado : "SIN SUSCRIPCIÓN");

                lista.add(dto);
            }
        }
        return lista;
    }
}