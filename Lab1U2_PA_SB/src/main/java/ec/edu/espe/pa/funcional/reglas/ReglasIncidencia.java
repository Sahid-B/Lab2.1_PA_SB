package ec.edu.espe.pa.funcional.reglas;

import ec.edu.espe.pa.funcional.modelo.Incidencia;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

// =========================================================================================
// ==== FASE 4: FUNCIONES DEL CASO PRÁCTICO ====
// Clase de utilidades que almacena contratos funcionales estándar como constantes inmutables.
// =========================================================================================
public final class ReglasIncidencia {

    // Constructor privado para evitar que la clase de utilidad sea instanciada
    private ReglasIncidencia() { }

    // Constante entera compartida: centraliza el umbral para no repetir números mágicos en las reglas.
    public static final int UMBRAL_PRIORIDAD_ALTA = 4;

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: DEFINICIÓN DE PREDICADOS (FILTROS Y CONDICIONES) ====
    // Predicate<T>: Entrada Incidencia -> Salida boolean (método abstracto test)
    // =========================================================================================
    
    // 1. Decide si la incidencia está abierta comparando cadenas de forma segura con equals
    public static final Predicate<Incidencia> ESTA_ABIERTA =
            incidencia -> "ABIERTA".equals(incidencia.estado());

    // 2. Decide si la prioridad de la incidencia alcanza o supera el umbral establecido (>= 4)
    public static final Predicate<Incidencia> PRIORIDAD_ALTA =
            incidencia -> incidencia.prioridad() >= UMBRAL_PRIORIDAD_ALTA;

    // 3. Combina por composición funcional: ESTA_ABIERTA AND PRIORIDAD_ALTA
    public static final Predicate<Incidencia> REQUIERE_ATENCION =
            ESTA_ABIERTA.and(PRIORIDAD_ALTA);

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: DEFINICIÓN DE FUNCIONES TRANSFORMADORAS ====
    // Function<T, R>: Entrada Incidencia -> Salida String (método abstracto apply)
    // =========================================================================================

    // 4. Transforma la incidencia en una cadena representativa con formato "ID - Titulo"
    public static final Function<Incidencia, String> RESUMEN =
            incidencia -> incidencia.id() + " - " + incidencia.titulo();

    // 5. Categoriza la incidencia en base a su nivel numérico de prioridad mediante switch expression
    // ==== FASE 4: SE COMPLETÓ EL TODO DE CATEGORIA CON SWITCH ARROW ====
    public static final Function<Incidencia, String> CATEGORIA =
            incidencia -> {
                // Evalúa la prioridad y retorna la clasificación textual de negocio
                return switch (incidencia.prioridad()) {
                    case 5 -> "CRITICA";
                    case 4 -> "ALTA";
                    case 2, 3 -> "MEDIA";
                    case 1 -> "BAJA";
                    default -> throw new IllegalStateException("Prioridad fuera de rango: " + incidencia.prioridad());
                };
            };

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: DEFINICIÓN DE ACCIÓN CONSUMIDORA ====
    // Consumer<T>: Entrada Incidencia -> Salida void (método abstracto accept)
    // =========================================================================================

    // 6. Imprime una notificación de alerta en consola usando el resumen de la incidencia
    public static final Consumer<Incidencia> NOTIFICAR =
            incidencia -> System.out.println("[ATENCION] " + RESUMEN.apply(incidencia));

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: GENERADOR SINTÉTICO (SUPPLIER) ====
    // Supplier<T>: Entrada void -> Salida Incidencia (método abstracto get)
    // Se utiliza AtomicInteger para permitir mutación de estado dentro de la clausura lambda.
    // =========================================================================================

    // 7. Fábrica generadora de incidencias sintéticas para pruebas y flujos infinitos de Stream
    public static Supplier<Incidencia> generadorSintetico() {
        // Caja atómica para mantener el contador auto-incremental sin violar la inmutabilidad de la lambda
        AtomicInteger contador = new AtomicInteger(1);
        // Estados válidos para alternar en cada elemento generado
        String[] estados = {"ABIERTA", "EN_PROCESO", "CERRADA"};
        // Retorna la lambda Supplier que crea una nueva Incidencia en cada invocación get()
        return () -> {
            // Incrementa y obtiene el índice actual
            int n = contador.getAndIncrement();
            // Retorna una nueva instancia con datos deterministas basados en el contador
            return new Incidencia("INC-%03d".formatted(n), "Incidencia sintética " + n,
                    estados[n % estados.length], (n % 5) + 1);
        };
    }

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: MÉTODO ESTÁTICO DE APOYO A REFERENCIAS ====
    // Permite ser referenciado funcionalmente como ReglasIncidencia::esCritica
    // =========================================================================================
    public static boolean esCritica(Incidencia incidencia) {
        // Retorna true si la prioridad es exactamente el valor máximo (5)
        return incidencia.prioridad() == 5;
    }
}
