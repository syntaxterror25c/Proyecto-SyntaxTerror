package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.Sala;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDAOImpl {

    // Método para insertar una nueva sala usando PreparedStatement
    public void insertar(Sala s) throws SQLException {
        // Hemos simplificado el SQL quitando el campo 'tipo'
        String sql = "INSERT INTO gym.salas (nombre, capacidad_maxima) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getNombre());
            ps.setInt(2, s.getCapacidadMaxima());

            ps.executeUpdate();
        }
    }

    // Método para obtener todas las salas usando PreparedStatement
    public List<Sala> listar() throws SQLException {
        List<Sala> lista = new ArrayList<>();
        // Ordenamos por ID para que siempre te salgan en el mismo orden (1, 2, 3)
        String sql = "SELECT id, nombre, capacidad_maxima FROM gym.salas ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             // Cambiado Statement por PreparedStatement por seguridad y consistencia
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sala s = new Sala();
                s.setId(rs.getInt("id"));
                s.setNombre(rs.getString("nombre"));
                s.setCapacidadMaxima(rs.getInt("capacidad_maxima"));

                lista.add(s);
            }
        }
        return lista;
    }
}