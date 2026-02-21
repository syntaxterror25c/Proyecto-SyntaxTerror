package es.cifpcarlos3.model;

import lombok.Data;
import java.time.LocalTime;

@Data
public class HorarioDetalleDTO {
    private int id;
    private String nombreClase;
    private int diaSemana;
    private LocalTime horaInicio;
    private int duracion;
    private String nombreTipo;
    private String nombreSala;
    private String nombreProfesor;
    private Integer aforoEspecifico;
}
