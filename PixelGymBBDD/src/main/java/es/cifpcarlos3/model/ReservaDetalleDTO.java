package es.cifpcarlos3.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservaDetalleDTO {
    private int idReserva;
    private String nombreCliente;
    private String nombreActividad;
    private LocalDate fechaSesion;
    private LocalTime horaSesion;
    private String nombreSala;
    private String estadoReserva; // 'CONFIRMADA', 'ASISTIDA', 'CANCELADA'
}