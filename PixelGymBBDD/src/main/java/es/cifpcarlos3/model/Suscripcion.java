// Archivo: Suscripcion.java
package es.cifpcarlos3.model;
import es.cifpcarlos3.model.enums.EstadoSuscripcion;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Suscripcion {
    private int id;
    private int idUsuario;
    private int idPlan;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoSuscripcion estado;
}