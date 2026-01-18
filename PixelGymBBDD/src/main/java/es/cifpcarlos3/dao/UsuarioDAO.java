package es.cifpcarlos3.dao;


import es.cifpcarlos3.model.*;

import java.sql.SQLException;
import java.util.List;


interface UsuarioDAO {
    void registrar(Usuario u) throws SQLException;
    Usuario buscarPorEmail(String email) throws SQLException;
}