package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.funcional.ProcesadorFuncional;
import ec.edu.espe.pa.funcional.imperativo.ProcesadorImperativo;
import ec.edu.espe.pa.funcional.modelo.Incidencia;

import java.util.List;

import static ec.edu.espe.pa.funcional.reglas.ReglasIncidencia.*;

// =========================================================================================
// ==== FASE 6: PROGRAMA PRINCIPAL DE VERIFICACIÓN (APP) ====
// Ejecuta lado a lado las versiones imperativa y funcional con el mismo conjunto de datos.
// =========================================================================================
public class App {

    public static void main(String[] args) {

        // =========================================================================================
        // ==== EN ESTE BLOQUE SE REALIZA: PREPARACIÓN DE DATOS SINTÉTICOS DE PRUEBA ====
        // Conjunto inmutable de incidencias con diversos estados y prioridades límites.
        // =========================================================================================
        List<Incidencia> datos = List.of(
                new Incidencia("INC-001", "Caída del servidor", "ABIERTA", 5),
                new Incidencia("INC-002", "Error tipográfico en el menú", "ABIERTA", 2),
                new Incidencia("INC-003", "Fallo de respaldo nocturno", "CERRADA", 5),
                new Incidencia("INC-004", "Lentitud en reportes", "ABIERTA", 3),
                new Incidencia("INC-005", "Error de acceso al sistema", "ABIERTA", 4),
                new Incidencia("INC-006", "Actualizar certificado", "EN_PROCESO", 4));

        // =========================================================================================
        // ==== EN ESTE BLOQUE SE REALIZA: EJECUCIÓN DE LA SOLUCIÓN IMPERATIVA ====
        // Procesa la lista usando bucles y condiciones imperativas clásicas.
        // =========================================================================================
        System.out.println("== Versión imperativa ==");
        new ProcesadorImperativo().mostrarQueRequierenAtencion(datos);

        // =========================================================================================
        // ==== EN ESTE BLOQUE SE REALIZA: EJECUCIÓN DE LA SOLUCIÓN FUNCIONAL ====
        // Procesa la lista usando el ProcesadorFuncional con streams y predicados de reglas.
        // =========================================================================================
        ProcesadorFuncional procesador = new ProcesadorFuncional();
        System.out.println("== Versión funcional ==");
        procesador.titulosQueRequierenAtencion(datos).forEach(System.out::println);

        // =========================================================================================
        // ==== EN ESTE BLOQUE SE REALIZA: DISPARO DE NOTIFICACIONES CON CONSUMER ====
        // Ejecuta la regla REQUIERE_ATENCION y efectúa la acción NOTIFICAR sobre cada incidencia seleccionada.
        // =========================================================================================
        System.out.println("== Notificaciones ==");
        procesador.ejecutar(datos, REQUIERE_ATENCION, NOTIFICAR);

        // =========================================================================================
        // ==== EN ESTE BLOQUE SE REALIZA: TRANSFORMACIÓN Y COMPOSICIÓN DINÁMICA ====
        // Filtra incidencias abiertas y las formatea combinando RESUMEN y CATEGORIA en una lambda.
        // =========================================================================================
        System.out.println("== Incidencias abiertas con su categoría ==");
        procesador.seleccionar(datos, ESTA_ABIERTA,
                        i -> RESUMEN.apply(i) + " [" + CATEGORIA.apply(i) + "]")
                .forEach(System.out::println);
    }
}
