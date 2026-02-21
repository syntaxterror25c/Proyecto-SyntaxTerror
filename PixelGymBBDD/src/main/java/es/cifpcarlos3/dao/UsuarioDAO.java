package es.cifpcarlos3.dao;


import es.cifpcarlos3.model.*;

import java.sql.SQLException;


interface UsuarioDAO {
    void registrar(Usuario u);
    Usuario buscarPorEmail(String email);
}