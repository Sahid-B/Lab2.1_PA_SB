package ec.edu.espe.pa.funcional.imperativo;

import ec.edu.espe.pa.funcional.modelo.Incidencia;

import java.util.ArrayList;
import java.util.List;

// =========================================================================================
// ==== FASE 1: SOLUCIÓN IMPERATIVA DE REFERENCIA ====
// Enfoque clásico donde se combinan la iteración, filtrado y transformación en un mismo bloque.
// Sirve como referencia inmutable para contrastar la versión funcional en las pruebas de equivalencia.
// =========================================================================================
public class ProcesadorImperativo {

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: FILTRADO Y EXTRACCIÓN IMPERATIVA DE TÍTULOS ====
    // Recorre secuencialmente la lista con bucle for y sentencia if acumulando en una nueva lista.
    // =========================================================================================
    public List<String> titulosQueRequierenAtencion(List<Incidencia> incidencias) {
        // Inicializa la lista acumuladora donde se almacenarán los títulos filtrados
        List<String> resultado = new ArrayList<>();
        // Itera sobre cada elemento de la colección de incidencias
        for (Incidencia incidencia : incidencias) {
            // Evalúa condición compuesta: estado abierto Y prioridad mayor o igual a 4
            if (incidencia.estado().equals("ABIERTA")
                    && incidencia.prioridad() >= 4) {
                // Agrega a la lista el título de la incidencia que cumplió el criterio
                resultado.add(incidencia.titulo());
            }
        }
        // Devuelve la lista con los títulos resultantes
        return resultado;
    }

    // =========================================================================================
    // ==== EN ESTE BLOQUE SE REALIZA: SALIDA POR CONSOLA DE INCIDENCIAS ====
    // Itera los títulos obtenidos y los muestra por la salida estándar mediante bucle for-each.
    // =========================================================================================
    public void mostrarQueRequierenAtencion(List<Incidencia> incidencias) {
        // Recorre cada título obtenido del método anterior
        for (String titulo : titulosQueRequierenAtencion(incidencias)) {
            // Imprime cada título en una línea de la consola
            System.out.println(titulo);
        }
    }
}
