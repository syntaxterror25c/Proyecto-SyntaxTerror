package pixelgym.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.google.cloud.firestore.annotation.PropertyName;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    // Campos principales del documento
    private String uid;
    private String nombre_usuario;
    private String nickname;
    private String email;
    private String telefono;
    private String imagen_url;

    // Objetos anidados como Mapas (según tu estructura actual)
    private Map<String, Object> suscripcion_actual;
    private Map<String, Object> consumo_actual;

    /**
     * Método para obtener los créditos de forma segura desde el mapa.
     * Extrae 'creditos' dentro de 'suscripcion_actual'.
     */
    public long getCreditos() {
        if (suscripcion_actual != null && suscripcion_actual.containsKey("creditos")) {
            Object cred = suscripcion_actual.get("creditos");
            if (cred instanceof Number) {
                return ((Number) cred).longValue();
            }
        }
        return 0L;
    }

    /**
     * Método para obtener el nombre del plan desde el mapa.
     * Extrae 'nombre_plan' dentro de 'suscripcion_actual'.
     */
    public String getNombrePlan() {
        if (suscripcion_actual != null && suscripcion_actual.containsKey("nombre_plan")) {
            return String.valueOf(suscripcion_actual.get("nombre_plan"));
        }
        return "SIN PLAN";
    }

    /**
     * Método para obtener la fecha de fin desde el mapa.
     * Extrae 'fecha_fin_plan' dentro de 'suscripcion_actual'.
     */
    public String getFechaFinPlan() {
        if (suscripcion_actual != null && suscripcion_actual.containsKey("fecha_fin_plan")) {
            return String.valueOf(suscripcion_actual.get("fecha_fin_plan"));
        }
        return "N/A";
    }
}