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

// =========================================================================================
// ==== FASE 7: PRUEBAS UNITARIAS DE REGLAS DE NEGOCIO (JUNIT 5) ====
// Evalúa individualmente cada contrato funcional en casos normales, límites y negativos.
// =========================================================================================
class ReglasIncidenciaTest {

    // Casos de prueba estándar reutilizados en varias pruebas
    private final Incidencia abiertaAlta = new Incidencia("INC-001", "Caída del servidor", "ABIERTA", 5);
    private final Incidencia abiertaBaja = new Incidencia("INC-002", "Error tipográfico", "ABIERTA", 2);
    private final Incidencia cerradaAlta = new Incidencia("INC-003", "Fallo de respaldo", "CERRADA", 5);

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: EVALUACIÓN DE REGLA REQUIERE_ATENCION ====
    // Solo debe responder true cuando la incidencia es ABIERTA y su prioridad >= 4.
    // =========================================================================================
    @Test
    void requiereAtencionSoloSiEstaAbiertaYEsDePrioridadAlta() {
        // Debe ser true porque cumple ambas condiciones
        assertTrue(REQUIERE_ATENCION.test(abiertaAlta));
        // Debe ser false porque la prioridad es baja (2)
        assertFalse(REQUIERE_ATENCION.test(abiertaBaja));
        // Debe ser false porque está cerrada
        assertFalse(REQUIERE_ATENCION.test(cerradaAlta));
    }

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: PRUEBA DE LÍMITES EN PRIORIDAD_ALTA ====
    // Verifica valores en el borde del umbral: 3 (debajo), 4 (en el umbral) y 5 (máximo).
    // =========================================================================================
    @ParameterizedTest
    @CsvSource({"3, false", "4, true", "5, true"})
    void prioridadAltaRespetaElUmbral(int prioridad, boolean esperado) {
        Incidencia incidencia = new Incidencia("INC-L", "Límite", "ABIERTA", prioridad);
        // Valida que el predicado retorne exactamente el booleano esperado según el valor límite
        assertEquals(esperado, PRIORIDAD_ALTA.test(incidencia));
    }

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: PRUEBA DE TRANSFORMACIÓN (RESUMEN Y CATEGORÍA) ====
    // Comprueba formato de resumen y mapeo a categoría crítica, incluyendo composition andThen.
    // =========================================================================================
    @Test
    void resumenYCategoriaTransformanCorrectamente() {
        // Comprueba la salida del Function RESUMEN
        assertEquals("INC-001 - Caída del servidor", RESUMEN.apply(abiertaAlta));
        // Comprueba la salida del Function CATEGORIA
        assertEquals("CRITICA", CATEGORIA.apply(abiertaAlta));
        // Comprueba composición con andThen(String::toUpperCase)
        assertEquals("INC-001 - CAÍDA DEL SERVIDOR",
                RESUMEN.andThen(String::toUpperCase).apply(abiertaAlta));
    }

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: PRUEBA DEL CONSUMER MEDIANTE BITÁCORA ====
    // Verifica efectos secundarios sin consola acumulando los identificadores en una lista.
    // =========================================================================================
    @Test
    void consumerRegistraSoloLasSeleccionadas() {
        // Lista acumuladora para registrar las incidencias procesadas
        List<String> bitacora = new ArrayList<>();
        // Consumer que agrega el ID a la bitácora
        Consumer<Incidencia> registrar = i -> bitacora.add(i.id());
        // Ejecuta el procesador funcional con el filtro REQUIERE_ATENCION
        new ProcesadorFuncional().ejecutar(List.of(abiertaAlta, abiertaBaja, cerradaAlta),
                REQUIERE_ATENCION, registrar);
        // Comprueba que únicamente INC-001 haya sido registrada
        assertEquals(List.of("INC-001"), bitacora);
    }

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: PRUEBA DE VALORES NEGATIVOS Y FUERA DE RANGO ====
    // Comprueba que los valores fuera de rango [1, 5] (como 0 y 6) sean rechazados.
    // =========================================================================================
    @ParameterizedTest
    @ValueSource(ints = {0, 6})
    void rechazaPrioridadesFueraDeRango(int prioridad) {
        // Verifica que se lance IllegalArgumentException para prioridades 0 y 6
        assertThrows(IllegalArgumentException.class,
                () -> new Incidencia("INC-X", "Inválida", "ABIERTA", prioridad));
    }

    // =========================================================================================
    // ==== FASE 7: SE COMPLETÓ EL TODO 1 - PRUEBA PARAMETRIZADA PARA CATEGORIA ====
    // Evalúa la función CATEGORIA con las prioridades 1 (BAJA), 3 (MEDIA), 4 (ALTA) y 5 (CRITICA).
    // =========================================================================================
    @ParameterizedTest
    @CsvSource({"1, BAJA", "3, MEDIA", "4, ALTA", "5, CRITICA"})
    void categoriaSegunPrioridad(int prioridad, String esperada) {
        Incidencia incidencia = new Incidencia("INC-C", "Categoría", "ABIERTA", prioridad);
        // Valida que el mapeo de categoría corresponda exactamente con la prioridad
        assertEquals(esperada, CATEGORIA.apply(incidencia));
    }

    // =========================================================================================
    // ==== FASE 7: SE COMPLETÓ EL TODO 2 - PRUEBA PARA generadorSintetico() ====
    // Verifica que dos llamadas a get() generen IDs distintos y prioridades válidas entre 1 y 5.
    // =========================================================================================
    @Test
    void generadorSinteticoEntregaIdsDistintosYPrioridadesValidas() {
        Supplier<Incidencia> generador = generadorSintetico();
        // Primera invocación al Supplier
        Incidencia primera = generador.get();
        // Segunda invocación al Supplier
        Incidencia segunda = generador.get();

        // Valida que los identificadores generados sean distintos y secuenciales
        assertNotEquals(primera.id(), segunda.id());
        // Valida que la prioridad de la primera esté en el rango permitido [1, 5]
        assertTrue(primera.prioridad() >= 1 && primera.prioridad() <= 5);
        // Valida que la prioridad de la segunda esté en el rango permitido [1, 5]
        assertTrue(segunda.prioridad() >= 1 && segunda.prioridad() <= 5);
    }

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: PRUEBA DE COMPOSICIÓN ADICIONAL (NEGATE Y OR) ====
    // Verifica el comportamiento de operadores lógicos funcionales sobre Predicate.
    // =========================================================================================
    @Test
    void negateYOrComponenReglasNuevas() {
        // Negación de ESTA_ABIERTA: equivale a NO abierta (CERRADA o EN_PROCESO)
        Predicate<Incidencia> noAbierta = ESTA_ABIERTA.negate();
        // Disyunción: requiere atención O es prioridad 5
        Predicate<Incidencia> revisarHoy = REQUIERE_ATENCION.or(i -> i.prioridad() == 5);

        // Valida que abiertaAlta no cumpla noAbierta
        assertFalse(noAbierta.test(abiertaAlta));
        // Valida que cerradaAlta sí cumpla noAbierta
        assertTrue(noAbierta.test(cerradaAlta));
        // Valida que cerradaAlta cumpla revisarHoy por tener prioridad 5
        assertTrue(revisarHoy.test(cerradaAlta));
        // Valida que abiertaBaja no cumpla ninguna de las dos condiciones
        assertFalse(revisarHoy.test(abiertaBaja));
    }
}
