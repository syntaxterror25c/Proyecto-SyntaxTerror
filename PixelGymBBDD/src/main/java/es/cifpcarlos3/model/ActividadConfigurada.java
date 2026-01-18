package es.cifpcarlos3.model;
import lombok.Data;
import java.time.LocalTime;

@Data
public class ActividadConfigurada {
    private int id;
    private String nombreClase;
    private int diaSemana;
    private LocalTime horaInicio;
    private int duracion;
    private int idTipoActividad;
    private int idSala;
    private int idProfesorFijo;
}

// Archivo: Sesion.java