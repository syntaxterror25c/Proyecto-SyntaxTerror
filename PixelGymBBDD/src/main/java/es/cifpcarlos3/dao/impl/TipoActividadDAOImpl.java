package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.TipoActividad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoActividadDAOImpl {

    public void insertar(TipoActividad tipo) throws SQLException {
        // Añadimos el esquema gym.
        String sql = "INSERT INTO gym.tipo_actividad (nombre, descripcion) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo.getNombre());
            ps.setString(2, tipo.getDescripcion());
            ps.executeUpdate();
        }
    }

    public List<TipoActividad> listar() throws SQLException {
        List<TipoActividad> lista = new ArrayList<>();
        String sql = "SELECT * FROM gym.tipo_actividad ORDER BY nombre";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                TipoActividad tipo = new TipoActividad();
                tipo.setId(rs.getInt("id"));
                tipo.setNombre(rs.getString("nombre"));
                tipo.setDescripcion(rs.getString("descripcion"));
                lista.add(tipo);
            }
        }
        return lista;
    }
}