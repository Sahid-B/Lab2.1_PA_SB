package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.modelo.Incidencia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProyectoConfiguradoTest {

    @Test
    void creaIncidenciaValida() {
        Incidencia incidencia = new Incidencia("INC-1", "Falla de impresora", "ABIERTA", 4);

        assertEquals("INC-1", incidencia.id());
        assertEquals("ABIERTA", incidencia.estado());
        assertEquals(4, incidencia.prioridad());
    }

    @Test
    void prioridadCeroLanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Incidencia("INC-2", "Prioridad inválida", "ABIERTA", 0));
    }
}
