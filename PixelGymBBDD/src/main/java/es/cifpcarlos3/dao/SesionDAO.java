package es.cifpcarlos3.dao;

import es.cifpcarlos3.model.*;

import java.sql.SQLException;
import java.util.List;

interface SesionDAO {
    void crearSesion(Sesion s) throws SQLException;
    List<Sesion> listarPorFecha(java.time.LocalDate fecha) throws SQLException;
}
