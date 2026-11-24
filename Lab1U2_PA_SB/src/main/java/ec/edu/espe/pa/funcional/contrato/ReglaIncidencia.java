package ec.edu.espe.pa.funcional.contrato;

import ec.edu.espe.pa.funcional.modelo.Incidencia;

// =========================================================================================
// ==== FASE 3: CONTRATO PROPIO DEL DOMINIO ====
// Se define una interfaz funcional propia para evaluar incidencias según reglas de negocio.
// La anotación @FunctionalInterface asegura en compilación la existencia de un solo método abstracto.
// =========================================================================================
@FunctionalInterface
public interface ReglaIncidencia {

    // Único método abstracto del contrato: recibe una Incidencia y determina si cumple la regla
    boolean evaluar(Incidencia incidencia);

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: COMPOSICIÓN DE REGLAS CON MÉTODO DEFAULT ====
    // Un método default proporciona lógica reutilizable sin romper el contrato de interfaz funcional.
    // Permite combinar dos reglas aplicando una conjunción lógica (AND).
    // =========================================================================================
    default ReglaIncidencia y(ReglaIncidencia otra) {
        // Retorna una nueva regla lambda que evalúa la regla actual Y la regla proporcionada
        return incidencia -> this.evaluar(incidencia) && otra.evaluar(incidencia);
    }
}
