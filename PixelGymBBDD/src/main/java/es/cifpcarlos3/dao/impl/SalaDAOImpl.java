package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.Sala;
import es.cifpcarlos3.model.enums.TipoSala;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDAOImpl {

    // Método para insertar una nueva sala
    public void insertar(Sala s) throws SQLException {
        String sql = "INSERT INTO gym.salas (nombre, capacidad_maxima, tipo) VALUES (?, ?, ?::gym.tipo_sala)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getNombre());
            ps.setInt(2, s.getCapacidadMaxima());
            // Convertimos el Enum de Java a String para la BBDD
            ps.setString(3, s.getTipo().name());

            ps.executeUpdate();
        }
    }

    // Método para obtener todas las salas
    public List<Sala> listar() throws SQLException {
        List<Sala> lista = new ArrayList<>();
        String sql = "SELECT * FROM gym.salas ORDER BY nombre";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Sala s = new Sala();
                s.setId(rs.getInt("id"));
                s.setNombre(rs.getString("nombre"));
                s.setCapacidadMaxima(rs.getInt("capacidad_maxima"));

                // Convertimos el String de la BBDD de vuelta al Enum de Java
                String tipoDb = rs.getString("tipo");
                if (tipoDb != null) {
                    s.setTipo(TipoSala.valueOf(tipoDb.toUpperCase()));
                }

                lista.add(s);
            }
        }
        return lista;
    }
}