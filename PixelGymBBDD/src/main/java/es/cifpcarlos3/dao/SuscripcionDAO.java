package es.cifpcarlos3.dao;

import es.cifpcarlos3.model.*;

import java.sql.SQLException;
import java.util.List;

interface SuscripcionDAO {
    void insertar(Suscripcion s);
    List<Suscripcion> listarPorUsuario(int idUsuario);
}