// Archivo: Usuario.java
package es.cifpcarlos3.model;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String passwordHash;
    private String telefono;
    private LocalDate fechaAlta;
}

