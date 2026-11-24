package ec.edu.espe.pa.funcional.modelo;

import java.util.Objects;

// =========================================================================================
// ==== FASE 1: PREPARACIÓN DEL PROYECTO - RECORD DEL DOMINIO ====
// Se define la entidad inmutable Incidencia utilizando la característica Record de Java.
// =========================================================================================
public record Incidencia(String id, String titulo, String estado, int prioridad) {

    // Constructor compacto para validar invariantes del dominio antes de instanciar el objeto
    public Incidencia {
        // Valida que el identificador no sea nulo; si lo es, arroja NullPointerException
        Objects.requireNonNull(id, "El id es obligatorio");
        // Valida que el estado no sea nulo; si lo es, arroja NullPointerException
        Objects.requireNonNull(estado, "El estado es obligatorio");
        // Valida que la prioridad se encuentre en el rango permitido de [1, 5]
        if (prioridad < 1 || prioridad > 5) {
            // Arroja IllegalArgumentException si la prioridad está fuera de los límites de negocio
            throw new IllegalArgumentException("La prioridad debe estar entre 1 y 5: " + prioridad);
        }
    }
}
