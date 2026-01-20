package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.Profesor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfesorDAOImpl {

    public void insertar(Profesor p) throws SQLException {
        String sql = "INSERT INTO gym.profesores (nombre, telefono, especialidad) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTelefono());
            ps.setString(3, p.getEspecialidad());
            ps.executeUpdate();
        }
    }

    public List<Profesor> listarProfesores() throws SQLException {
        List<Profesor> lista = new ArrayList<>();

        // 1. Añadimos 'telefono' a la consulta SQL
        String sql = "SELECT id, nombre, especialidad, telefono FROM gym.profesores ORDER BY nombre ASC";

        try (Connection conn = DBConnection.getConnection();
             // Usamos PreparedStatement como siempre
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Profesor p = new Profesor();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setEspecialidad(rs.getString("especialidad"));

                // 2. Leemos la columna que acabamos de añadir a la BD
                p.setTelefono(rs.getString("telefono"));

                lista.add(p);
            }
        }
        return lista;
    }
}