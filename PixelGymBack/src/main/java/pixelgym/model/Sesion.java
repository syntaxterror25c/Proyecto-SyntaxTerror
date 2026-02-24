package pixelgym.model;

import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sesion {

    @Exclude // El ID del documento no es un campo dentro del documento
    private String id;

    private String fecha;
    private String sala;
    private long coste;

    @PropertyName("nombre_actividad")
    private String nombre_actividad;

    @PropertyName("nombre_profesor")
    private String nombre_profesor;

    @PropertyName("hora_inicio")
    private String hora_inicio;

    @PropertyName("capacidad_maxima")
    private long capacidad_maxima;

    @PropertyName("plazas_ocupadas")
    private long plazas_ocupadas;

    @PropertyName("estado_sesion")
    private String estado_sesion;

    @PropertyName("imagen_url")
    private String imagen_url;
}