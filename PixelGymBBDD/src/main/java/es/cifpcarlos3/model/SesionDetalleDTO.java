package es.cifpcarlos3.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SesionDetalleDTO {
    private int idSesion;
    private String nombreClase;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private String nombreSala;
    private String nombreProfesor;
    private int aforoMaximo;
    private int plazasLibres;
    private int idActividadConfigurada;
    private String descripcion;
    private String estado;
    private int duracionMinutos;
}