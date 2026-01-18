package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.Profesor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfesorDAOImpl {

    public void insertar(Profesor p) throws SQLException {
        String sql = "INSERT INTO profesores (nombre, telefono, especialidad) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTelefono());
            ps.setString(3, p.getEspecialidad());
            ps.executeUpdate();
        }
    }

    public List<Profesor> listar() throws SQLException {
        List<Profesor> lista = new ArrayList<>();
        String sql = "SELECT * FROM gym.profesores";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Profesor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("especialidad")
                ));
            }
        }
        return lista;
    }
}