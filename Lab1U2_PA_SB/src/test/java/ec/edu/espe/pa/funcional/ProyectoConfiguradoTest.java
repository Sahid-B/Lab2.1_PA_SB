package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.modelo.Incidencia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// =========================================================================================
// ==== FASE 1: PRUEBAS BÁSICAS DE CONFIGURACIÓN DEL PROYECTO ====
// Verifica que el entorno JUnit 5 opere correctamente y que el Record Incidencia valide sus datos.
// =========================================================================================
class ProyectoConfiguradoTest {

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: VERIFICACIÓN DE INSTANCIACIÓN VÁLIDA ====
    // Comprueba que los getters automáticos del record retornen los valores proporcionados.
    // =========================================================================================
    @Test
    void creaIncidenciaValida() {
        // Instancia una incidencia con datos correctos dentro del rango
        Incidencia incidencia = new Incidencia("INC-1", "Falla de impresora", "ABIERTA", 4);

        // Verifica que el id coincida con el asignado
        assertEquals("INC-1", incidencia.id());
        // Verifica que el estado coincida con el asignado
        assertEquals("ABIERTA", incidencia.estado());
        // Verifica que la prioridad coincida con el valor asignado
        assertEquals(4, incidencia.prioridad());
    }

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: PRUEBA DE CASO LÍMITE NEGATIVO (VALOR 0) ====
    // Asegura que el constructor compacto lance IllegalArgumentException ante valores ilegales.
    // =========================================================================================
    @Test
    void prioridadCeroLanzaIllegalArgumentException() {
        // Valida que se arroje la excepción esperada al pasar prioridad 0
        assertThrows(IllegalArgumentException.class,
                () -> new Incidencia("INC-2", "Prioridad inválida", "ABIERTA", 0));
    }
}
