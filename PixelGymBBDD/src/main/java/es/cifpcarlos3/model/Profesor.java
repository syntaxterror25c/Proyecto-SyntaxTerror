// Archivo: Profesor.java
package es.cifpcarlos3.model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profesor {
    private int id;
    private String nombre;
    private String telefono;
    private String especialidad;
}
