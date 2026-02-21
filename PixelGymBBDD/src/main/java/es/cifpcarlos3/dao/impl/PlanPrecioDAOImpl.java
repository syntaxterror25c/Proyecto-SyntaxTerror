package es.cifpcarlos3.dao.impl;

import es.cifpcarlos3.config.DBConnection;
import es.cifpcarlos3.model.PlanPrecio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanPrecioDAOImpl {
    public void insertar(PlanPrecio plan) throws SQLException {
        String sql = "INSERT INTO gym.plan_precios (nombre_plan, precio_mensual, limite_sesiones) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, plan.getNombrePlan());
            ps.setDouble(2, plan.getPrecioMensual());
            ps.setInt(3, plan.getLimiteSesiones());
            ps.executeUpdate();
        }
    }
    // Añade esto a tu PlanPrecioDAOImpl.java
    public List<PlanPrecio> listarTodos() throws SQLException {
        List<PlanPrecio> lista = new ArrayList<>();

        // Usamos PreparedStatement como hemos quedado para todas las consultas SQL
        String sql = "SELECT id, nombre_plan, precio_mensual, limite_sesiones FROM gym.plan_precios WHERE activo = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PlanPrecio p = new PlanPrecio();
                p.setId(rs.getInt("id"));
                p.setNombrePlan(rs.getString("nombre_plan"));
                p.setPrecioMensual(rs.getDouble("precio_mensual"));
                p.setLimiteSesiones(rs.getInt("limite_sesiones"));
                lista.add(p);
            }
        }
        return lista;
    }
}