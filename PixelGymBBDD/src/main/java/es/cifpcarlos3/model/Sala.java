
// Archivo: Sala.java
package es.cifpcarlos3.model;
import es.cifpcarlos3.model.enums.TipoSala;
import lombok.Data;

@Data
public class Sala {
    private int id;
    private String nombre;
    private int capacidadMaxima;
    private TipoSala tipo;
}
