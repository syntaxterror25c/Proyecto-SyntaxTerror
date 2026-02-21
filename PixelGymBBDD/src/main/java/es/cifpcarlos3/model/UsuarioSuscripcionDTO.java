package es.cifpcarlos3.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioSuscripcionDTO {
    private int idUsuario;
    private String nombreUsuario;
    private String email;
    private String nombrePlan;
    private LocalDate fechaFin;
    private String estadoSuscripcion; // 'ACTIVA', 'EXPIRADA', 'PENDIENTE'
}