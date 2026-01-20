package es.cifpcarlos3.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sala {
    private int id;
    private String nombre;
    private int capacidadMaxima;
}
