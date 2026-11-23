package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.funcional.ProcesadorFuncional;
import ec.edu.espe.pa.funcional.modelo.Incidencia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.function.*;

import static ec.edu.espe.pa.funcional.reglas.ReglasIncidencia.*;
import static org.junit.jupiter.api.Assertions.*;

class ReglasIncidenciaTest {

    private final Incidencia abiertaAlta = new Incidencia("INC-001", "Caída del servidor", "ABIERTA", 5);
    private final Incidencia abiertaBaja = new Incidencia("INC-002", "Error tipográfico", "ABIERTA", 2);
    private final Incidencia cerradaAlta = new Incidencia("INC-003", "Fallo de respaldo", "CERRADA", 5);

    @Test
    void requiereAtencionSoloSiEstaAbiertaYEsDePrioridadAlta() {
        assertTrue(REQUIERE_ATENCION.test(abiertaAlta));
        assertFalse(REQUIERE_ATENCION.test(abiertaBaja));
        assertFalse(REQUIERE_ATENCION.test(cerradaAlta));
    }

    @ParameterizedTest
    @CsvSource({"3, false", "4, true", "5, true"})
    void prioridadAltaRespetaElUmbral(int prioridad, boolean esperado) {
        Incidencia incidencia = new Incidencia("INC-L", "Límite", "ABIERTA", prioridad);
        assertEquals(esperado, PRIORIDAD_ALTA.test(incidencia));
    }

    @Test
    void resumenYCategoriaTransformanCorrectamente() {
        assertEquals("INC-001 - Caída del servidor", RESUMEN.apply(abiertaAlta));
        assertEquals("CRITICA", CATEGORIA.apply(abiertaAlta));
        assertEquals("INC-001 - CAÍDA DEL SERVIDOR",
                RESUMEN.andThen(String::toUpperCase).apply(abiertaAlta));
    }

    @Test
    void consumerRegistraSoloLasSeleccionadas() {
        List<String> bitacora = new ArrayList<>();
        Consumer<Incidencia> registrar = i -> bitacora.add(i.id());
        new ProcesadorFuncional().ejecutar(List.of(abiertaAlta, abiertaBaja, cerradaAlta),
                REQUIERE_ATENCION, registrar);
        assertEquals(List.of("INC-001"), bitacora);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6})
    void rechazaPrioridadesFueraDeRango(int prioridad) {
        assertThrows(IllegalArgumentException.class,
                () -> new Incidencia("INC-X", "Inválida", "ABIERTA", prioridad));
    }

    // TODO 1 (completado): CATEGORIA para las prioridades 1, 3, 4 y 5
    @ParameterizedTest
    @CsvSource({"1, BAJA", "3, MEDIA", "4, ALTA", "5, CRITICA"})
    void categoriaSegunPrioridad(int prioridad, String esperada) {
        Incidencia incidencia = new Incidencia("INC-C", "Categoría", "ABIERTA", prioridad);
        assertEquals(esperada, CATEGORIA.apply(incidencia));
    }

    // TODO 2 (completado): el Supplier entrega ids distintos y prioridades entre 1 y 5
    @Test
    void generadorSinteticoEntregaIdsDistintosYPrioridadesValidas() {
        Supplier<Incidencia> generador = generadorSintetico();
        Incidencia primera = generador.get();
        Incidencia segunda = generador.get();
        assertNotEquals(primera.id(), segunda.id());
        assertTrue(primera.prioridad() >= 1 && primera.prioridad() <= 5);
        assertTrue(segunda.prioridad() >= 1 && segunda.prioridad() <= 5);
    }

    // Composición adicional: negate y or
    @Test
    void negateYOrComponenReglasNuevas() {
        Predicate<Incidencia> noAbierta = ESTA_ABIERTA.negate();
        Predicate<Incidencia> revisarHoy = REQUIERE_ATENCION.or(i -> i.prioridad() == 5);
        assertFalse(noAbierta.test(abiertaAlta));
        assertTrue(noAbierta.test(cerradaAlta));
        assertTrue(revisarHoy.test(cerradaAlta));
        assertFalse(revisarHoy.test(abiertaBaja));
    }
}
