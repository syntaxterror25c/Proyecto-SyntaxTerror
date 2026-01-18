package es.cifpcarlos3.dao;

import es.cifpcarlos3.model.*;

import java.sql.SQLException;
import java.util.List;

interface TipoActividadDAO {
    void insertar(TipoActividad ta) throws SQLException;
    List<TipoActividad> listar() throws SQLException;
}