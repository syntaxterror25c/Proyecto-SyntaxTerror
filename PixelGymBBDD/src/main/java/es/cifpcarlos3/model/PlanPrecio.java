package es.cifpcarlos3.model;

import lombok.Data;

@Data
public class PlanPrecio {
    private int id;
    private String nombrePlan;      // Genera getNombrePlan()
    private double precioMensual;   // Genera getPrecioMensual()
    private int limiteSesiones;  // Genera getLimiteSesiones()
}