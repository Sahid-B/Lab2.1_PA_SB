package ec.edu.espe.pa.funcional.contrato;

import ec.edu.espe.pa.funcional.modelo.Incidencia;

// =========================================================================================
// ==== FASE 5: REFERENCIA A CONSTRUCTORES Y CONTRATO PROPIO ====
// java.util.function no dispone de una interfaz estándar que reciba 4 parámetros.
// Se define FabricaIncidencia para soportar referencias al constructor Incidencia::new.
// =========================================================================================
@FunctionalInterface
public interface FabricaIncidencia {

    // Método abstracto para instanciar una Incidencia a partir de sus 4 atributos
    Incidencia crear(String id, String titulo, String estado, int prioridad);
}
