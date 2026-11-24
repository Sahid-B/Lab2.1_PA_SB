package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.funcional.ProcesadorFuncional;
import ec.edu.espe.pa.funcional.imperativo.ProcesadorImperativo;
import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.reglas.ReglasIncidencia;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

// =========================================================================================
// ==== FASE 7: PRUEBAS DE EQUIVALENCIA ENTRE IMPERATIVO Y FUNCIONAL ====
// Garantiza que la refactorización funcional no alteró el comportamiento original del sistema.
// Compara las salidas de ambas implementaciones alimentadas con los mismos conjuntos de datos.
// =========================================================================================
class EquivalenciaTest {

    // Instancia de la referencia original imperativa
    private final ProcesadorImperativo imperativo = new ProcesadorImperativo();
    // Instancia de la versión refactorizada funcional
    private final ProcesadorFuncional funcional = new ProcesadorFuncional();

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: GENERACIÓN DE ESCENARIOS DE PRUEBA ====
    // Provee diversos conjuntos: lista vacía, un solo elemento, mezcla de casos y datos sintéticos.
    // =========================================================================================
    static Stream<List<Incidencia>> escenarios() {
        return Stream.of(
                // Escenario 1: Lista vacía
                List.of(),
                // Escenario 2: Lista con un único elemento
                List.of(new Incidencia("INC-001", "Caída del servidor", "ABIERTA", 5)),
                // Escenario 3: Conjunto heterogéneo con múltiples combinaciones de estado y prioridad
                List.of(new Incidencia("INC-004", "Lentitud en reportes", "ABIERTA", 3),
                        new Incidencia("INC-005", "Error de acceso", "ABIERTA", 4),
                        new Incidencia("INC-003", "Fallo de respaldo", "CERRADA", 5)),
                // Escenario 4: 50 elementos generados dinámicamente mediante el generador sintético
                Stream.generate(ReglasIncidencia.generadorSintetico()).limit(50).toList());
    }

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: VERIFICACIÓN DE EQUIVALENCIA Y SIN EFECTOS COLATERALES ====
    // Evalúa que ambas versiones devuelvan los mismos títulos y que la lista original no mute.
    // =========================================================================================
    @ParameterizedTest
    @MethodSource("escenarios")
    void ambasSolucionesProducenElMismoResultado(List<Incidencia> datos) {
        // Crea una copia inmutable de los datos para asegurar que no se muten durante el procesamiento
        List<Incidencia> copia = List.copyOf(datos);

        // Obtiene los títulos procesados por la versión imperativa
        List<String> esperado = imperativo.titulosQueRequierenAtencion(datos);
        // Obtiene los títulos procesados por la versión funcional
        List<String> obtenido = funcional.titulosQueRequierenAtencion(datos);

        // Valida que ambas listas contengan exactamente los mismos elementos en idéntico orden
        assertEquals(esperado, obtenido);
        // Valida que los datos de entrada permanezcan inalterados tras la ejecución
        assertEquals(copia, datos);
    }
}
