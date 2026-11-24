package ec.edu.espe.pa.funcional.reglas;

import ec.edu.espe.pa.funcional.contrato.ReglaIncidencia;
import ec.edu.espe.pa.funcional.modelo.Incidencia;

// =========================================================================================
// ==== FASE 3: FORMA 1 - IMPLEMENTACIÓN MEDIANTE CLASE CON NOMBRE ====
// Cumple el contrato ReglaIncidencia a través de una clase tradicional con nombre.
// =========================================================================================
public class PrioridadAlta implements ReglaIncidencia {

    // Sobrescribe el método abstracto evaluar aplicando la condición de prioridad >= 4
    @Override
    public boolean evaluar(Incidencia incidencia) {
        // Retorna true si la prioridad de la incidencia es mayor o igual a 4
        return incidencia.prioridad() >= 4;
    }
}
