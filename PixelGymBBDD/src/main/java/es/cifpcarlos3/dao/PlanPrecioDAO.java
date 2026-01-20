package es.cifpcarlos3.dao;


import es.cifpcarlos3.model.*;

import java.sql.SQLException;
import java.util.List;

interface PlanPrecioDAO {
    void insertar(PlanPrecio p);
    List<PlanPrecio> listar();
}
