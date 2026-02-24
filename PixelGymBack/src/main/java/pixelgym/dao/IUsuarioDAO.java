package pixelgym.dao;

import pixelgym.model.Usuario;
import java.util.List;

public interface IUsuarioDAO {
    List<Usuario> obtenerTodos() throws Exception;
    void actualizarCreditos(String uid, long nuevosCreditos) throws Exception;
    String buscarUsuarioPorEmail(String email) throws Exception;
    Usuario buscarPorUid(String uid) throws Exception;
    long obtenerSumaTotalCreditos() throws Exception;
}