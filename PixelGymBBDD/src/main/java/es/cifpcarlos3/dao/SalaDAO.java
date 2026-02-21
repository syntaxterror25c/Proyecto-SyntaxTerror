package es.cifpcarlos3.dao;


import es.cifpcarlos3.model.*;

import java.sql.SQLException;
import java.util.List;

interface SalaDAO {
    void insertar(Sala s);
    List<Sala> listar();
}
