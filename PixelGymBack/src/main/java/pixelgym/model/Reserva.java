package pixelgym.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    // Si el campo se llama igual en Java que en Firebase (como uid o coste),
    // no hace falta @PropertyName.
    private String uid;
    private long coste;

    @PropertyName("nombre_actividad")
    private String nombre_actividad;

    @PropertyName("nombre_profesor")
    private String nombre_profesor;

    @PropertyName("fecha_sesion")
    private String fecha_sesion;

    @PropertyName("hora_inicio")
    private String hora_inicio;

    @PropertyName("estado_reserva")
    private String estado_reserva;

    @PropertyName("id_reserva")
    private String id_reserva;

    @PropertyName("id_sesion_reservada")
    private String id_sesion_reservada;

    @PropertyName("mes_anio")
    private String mes_anio;

    @PropertyName("imagen_url")
    private String imagen_url;

    @PropertyName("fecha_creacion_reserva")
    private Timestamp fecha_creacion_reserva;
}