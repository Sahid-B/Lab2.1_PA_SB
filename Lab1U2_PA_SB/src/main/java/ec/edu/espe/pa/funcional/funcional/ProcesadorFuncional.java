package ec.edu.espe.pa.funcional.funcional;

import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.reglas.ReglasIncidencia;

import java.util.List;
import java.util.function.*;

// =========================================================================================
// ==== FASE 6: REFACTORIZACIÓN A ENFOQUE FUNCIONAL ====
// Desacopla las operaciones en funciones de orden superior mediante la API Stream de Java.
// Las reglas y acciones son suministradas como parámetros sin acoplamiento a una implementación fija.
// =========================================================================================
public class ProcesadorFuncional {

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: FILTRADO Y TRANSFORMACIÓN GENÉRICA (SELECCIONAR) ====
    // Recibe funciones como parámetros: un Predicate para filtrar y una Function para mapear/transformar.
    // =========================================================================================
    public <R> List<R> seleccionar(List<Incidencia> incidencias,
                                   Predicate<Incidencia> regla,
                                   Function<Incidencia, R> transformacion) {
        // Abre el Stream sobre la lista de entrada
        return incidencias.stream()
                // Aplica el filtro funcional con el predicado suministrado
                .filter(regla)
                // Transforma cada elemento al tipo de retorno R según la función proporcionada
                .map(transformacion)
                // Recolecta los elementos en una lista inmutable (Java 16+)
                .toList();
    }

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: CONSULTA ESPECÍFICA REUTILIZANDO EL MÉTODO GENÉRICO ====
    // Combina la regla de atención y la referencia a método Incidencia::titulo.
    // =========================================================================================
    public List<String> titulosQueRequierenAtencion(List<Incidencia> incidencias) {
        // Invoca el método seleccionar pasando el Predicate REQUIERE_ATENCION y la Function Incidencia::titulo
        return seleccionar(incidencias, ReglasIncidencia.REQUIERE_ATENCION, Incidencia::titulo);
    }

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: ACCIÓN Y EFECTO SECUNDARIO (EJECUTAR) ====
    // Filtra las incidencias con un Predicate y ejecuta una acción terminal mediante un Consumer.
    // =========================================================================================
    public void ejecutar(List<Incidencia> incidencias,
                         Predicate<Incidencia> regla,
                         Consumer<Incidencia> accion) {
        // Filtra los elementos según la regla y aplica la acción del consumidor a cada coincidencia
        incidencias.stream().filter(regla).forEach(accion);
    }
}
