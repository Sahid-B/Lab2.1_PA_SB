package ec.edu.espe.pa.funcional.reglas;

import ec.edu.espe.pa.funcional.modelo.Incidencia;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

public final class ReglasIncidencia {

    private ReglasIncidencia() { }

    // Constante entera compartida: centraliza el umbral para no repetir números mágicos en las reglas.
    public static final int UMBRAL_PRIORIDAD_ALTA = 4;

    // 1. Decide (Incidencia -> boolean): Predicate
    // Recibe una Incidencia y devuelve boolean, por eso es Predicate: solo decide si está abierta, sin transformar datos ni producir efectos secundarios.
    public static final Predicate<Incidencia> ESTA_ABIERTA =
            incidencia -> "ABIERTA".equals(incidencia.estado());

    // 2. Decide (Incidencia -> boolean): Predicate
    // Recibe una Incidencia y devuelve boolean, por eso es Predicate: solo decide si la prioridad alcanza el umbral, sin efectos secundarios.
    public static final Predicate<Incidencia> PRIORIDAD_ALTA =
            incidencia -> incidencia.prioridad() >= UMBRAL_PRIORIDAD_ALTA;

    // 3. Combina las dos reglas anteriores
    // Sigue siendo Predicate porque recibe Incidencia y devuelve boolean; se obtiene por composición con and:
    // ESTA_ABIERTA.and(PRIORIDAD_ALTA) exige que ambas condiciones se cumplan a la vez.
    public static final Predicate<Incidencia> REQUIERE_ATENCION =
            ESTA_ABIERTA.and(PRIORIDAD_ALTA);

    // 4. Transforma (Incidencia -> String): Function
    // Recibe una Incidencia y devuelve String, por eso es Function: transforma el objeto en un resumen legible, sin decidir ni imprimir.
    public static final Function<Incidencia, String> RESUMEN =
            incidencia -> incidencia.id() + " - " + incidencia.titulo();

    // 5. Calcula una categoría (Incidencia -> String): Function
    // Recibe una Incidencia y devuelve String, por eso es Function: mapea la prioridad a una etiqueta de categoría mediante un switch con flechas.
    public static final Function<Incidencia, String> CATEGORIA =
            incidencia -> {
                return switch (incidencia.prioridad()) {
                    case 5 -> "CRITICA";
                    case 4 -> "ALTA";
                    case 2, 3 -> "MEDIA";
                    case 1 -> "BAJA";
                    default -> throw new IllegalStateException("Prioridad fuera de rango: " + incidencia.prioridad());
                };
            };

    // 6. Actúa sin devolver valor (Incidencia -> void): Consumer
    // Recibe una Incidencia y no devuelve nada, por eso es Consumer: su propósito es el efecto secundario de imprimir la notificación.
    public static final Consumer<Incidencia> NOTIFICAR =
            incidencia -> System.out.println("[ATENCION] " + RESUMEN.apply(incidencia));

    // 7. Provee datos sin recibir entrada (() -> Incidencia): Supplier
    //    Es además un ejemplo de función como valor de retorno
    // No recibe argumentos y devuelve Incidencia, por eso es Supplier; se retorna como valor para poder reutilizarlo en Stream.generate.
    // Se usa AtomicInteger porque una variable int local no puede modificarse dentro de la lambda: debe ser final o efectivamente final.
    // AtomicInteger permite mutar su contenido sin reasignar la referencia capturada.
    public static Supplier<Incidencia> generadorSintetico() {
        AtomicInteger contador = new AtomicInteger(1);
        String[] estados = {"ABIERTA", "EN_PROCESO", "CERRADA"};
        return () -> {
            int n = contador.getAndIncrement();
            return new Incidencia("INC-%03d".formatted(n), "Incidencia sintética " + n,
                    estados[n % estados.length], (n % 5) + 1);
        };
    }

    // Método estático usado como referencia a método (ReglasIncidencia::esCritica)
    public static boolean esCritica(Incidencia incidencia) {
        return incidencia.prioridad() == 5;
    }
}
