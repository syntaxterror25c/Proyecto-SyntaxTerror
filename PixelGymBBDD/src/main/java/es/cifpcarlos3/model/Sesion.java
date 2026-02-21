
package es.cifpcarlos3.model;
import es.cifpcarlos3.model.enums.EstadoSesion;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Sesion {
    private int id;
    private LocalDate fecha;
    private int idActividadConfigurada;
    private Integer idProfesorSustituto;
    private EstadoSesion estado;
    private Integer aforoEspecifico;
}