package pixelgym.dao;

import pixelgym.model.Reserva;
import java.util.List;

public interface IReservaDAO {
    List<Reserva> obtenerTodas() throws Exception;
    List<Reserva> buscarPorEmail(String email) throws Exception;
    List<Reserva> buscarPorFecha(String fecha) throws Exception;
    List<Reserva> buscarPorActividad(String nombreActividad) throws Exception;
    List<Reserva> buscarPorProfesor(String nombreProfesor) throws Exception;
    List<Reserva> buscarPorUid(String uid) throws Exception;
}