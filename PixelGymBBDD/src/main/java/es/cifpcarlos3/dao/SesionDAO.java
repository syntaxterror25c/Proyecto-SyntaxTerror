package es.cifpcarlos3.dao;

import es.cifpcarlos3.model.Sesion;
import java.sql.Connection; // IMPORTANTE: java.sql
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface SesionDAO {
    // El contrato ahora devuelve int
    int crearSesion(Connection conn, LocalDate fecha, int idConfig, int idSala, Integer aforoEspec) throws SQLException;

    List<Sesion> listarPorFecha(LocalDate fecha) throws SQLException;
}