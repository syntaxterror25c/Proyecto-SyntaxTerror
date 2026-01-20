package es.cifpcarlos3.dao;

import es.cifpcarlos3.model.*;

import java.sql.SQLException;
import java.util.List;

interface ActividadConfiguradaDAO {
    void insertar(ActividadConfigurada ac);
    List<ActividadConfigurada> listarHorario();
}
