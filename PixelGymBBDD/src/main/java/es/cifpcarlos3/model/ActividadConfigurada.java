package es.cifpcarlos3.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActividadConfigurada {
    private int id;
    private String nombreClase;
    private int diaSemana;
    private LocalTime horaInicio; // Lo mantenemos como LocalTime para que sea fácil de usar en Java
    private int duracion;         // Minutos (ej. 30, 60)
    private int idTipoActividad;
    private int idSala;
    private int idProfesorFijo;
    private Integer aforoEspecifico;

    public int getHoraInicioEnMinutos() {
        if (this.horaInicio == null) return 0;
        return (this.horaInicio.getHour() * 60) + this.horaInicio.getMinute();
    }

    // Método inverso para cuando leas de la base de datos (opcional pero útil)
    public void setHoraInicioDesdeMinutos(int minutos) {
        int horas = minutos / 60;
        int mins = minutos % 60;
        this.horaInicio = LocalTime.of(horas, mins);
    }
}