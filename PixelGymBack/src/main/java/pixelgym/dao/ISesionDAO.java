package pixelgym.dao;

import pixelgym.model.Sesion;
import java.util.List;

public interface ISesionDAO {
    List<Sesion> obtenerTodas() throws Exception;
    List<Sesion> buscarPorFecha(String fecha) throws Exception;
    List<Sesion> buscarPorActividad(String nombreActividad) throws Exception;
    List<Sesion> buscarPorProfesor(String nombreProfesor) throws Exception;
    List<Sesion> buscarPorSala(String sala) throws Exception;
    void crearSesionProgramada(Sesion s, String idPersonalizado) throws Exception;
}