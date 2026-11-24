package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.contrato.FabricaIncidencia;
import ec.edu.espe.pa.funcional.contrato.ReglaIncidencia;
import ec.edu.espe.pa.funcional.reglas.PrioridadAlta;
import ec.edu.espe.pa.funcional.reglas.ReglasIncidencia;
import ec.edu.espe.pa.funcional.modelo.Incidencia;

import java.util.function.*;
import java.util.*;
import java.util.stream.Stream;

import static ec.edu.espe.pa.funcional.reglas.ReglasIncidencia.*;

// =========================================================================================
// ==== CLASE DEMOSTRACION: FASES 2, 3 Y 5 ====
// Centraliza las demostraciones prácticas de interfaces funcionales, sintaxis de lambdas,
// formas de implementar contratos propios, composición y referencias a métodos.
// =========================================================================================
public class Demostracion {

    public static void main(String[] args) {

        // =========================================================================================
        // ==== FASE 2: DEMOSTRACIÓN - LAS CUATRO INTERFACES ESTÁNDAR Y SINTAXIS LAMBDA ====
        // 1. Definición de las 4 interfaces principales de java.util.function
        // =========================================================================================
        Predicate<Incidencia> estaAbierta = incidencia -> "ABIERTA".equals(incidencia.estado());
        Function<Incidencia, String> obtenerResumen = incidencia -> incidencia.id() + " - " + incidencia.titulo();
        Consumer<Incidencia> mostrar = incidencia -> System.out.println(incidencia.titulo());
        Supplier<Incidencia> crearEjemplo = () -> new Incidencia("INC-001", "Error de acceso", "ABIERTA", 3);

        // =========================================================================================
        // ==== EN ESTE BLOQUE SE REALIZA: INVOCACIÓN DE MÉTODOS ABSTRACTOS DE CADA INTERFAZ ====
        // Supplier -> get(), Predicate -> test(), Function -> apply(), Consumer -> accept()
        // =========================================================================================
        Incidencia ejemplo = crearEjemplo.get();
        boolean abierta = estaAbierta.test(ejemplo);
        String resumen = obtenerResumen.apply(ejemplo);

        System.out.println("Incidencia creada por el Supplier: " + ejemplo);
        System.out.println("¿Está abierta? (Predicate): " + abierta);
        System.out.println("Resumen (Function): " + resumen);
        System.out.println("Título impreso por el Consumer:");
        mostrar.accept(ejemplo);

        // =========================================================================================
        // ==== EN ESTE BLOQUE SE REALIZA: VARIANTES SINTÁCTICAS DE EXPRESIONES LAMBDA ====
        // Demuestra expresión directa, bloque con return, tipos explícitos e inferencia con var.
        // =========================================================================================
        Predicate<Incidencia> altaExpresion = i -> i.prioridad() >= 4;
        Predicate<Incidencia> altaBloque = i -> {
            int p = i.prioridad();
            return p >= 4;
        };
        Predicate<Incidencia> altaTipoDeclarado = (Incidencia i) -> i.prioridad() >= 4;
        Predicate<Incidencia> altaVar = (var i) -> i.prioridad() >= 4;

        System.out.println("Lambda de una expresión (prioridad >= 4): " + altaExpresion.test(ejemplo));
        System.out.println("Lambda con bloque (prioridad >= 4): " + altaBloque.test(ejemplo));
        System.out.println("Tipo declarado (Incidencia i) (prioridad >= 4): " + altaTipoDeclarado.test(ejemplo));
        System.out.println("Tipo declarado (var i) (prioridad >= 4): " + altaVar.test(ejemplo));

        // =========================================================================================
        // ==== EN ESTE BLOQUE SE REALIZA: CAPTURA DE VARIABLES EFECTIVAMENTE FINALES ====
        // Las variables locales capturadas por una lambda no deben ser reasignadas en el método.
        // =========================================================================================
        int umbral = 4;
        Predicate<Incidencia> superaUmbral = i -> i.prioridad() >= umbral;
        // umbral = 5; // Si se descomenta, no compila: variable must be final or effectively final

        System.out.println("Captura de umbral (" + umbral + ") (prioridad >= umbral): " + superaUmbral.test(ejemplo));

        // =========================================================================================
        // ==== FASE 3: CONTRATO PROPIO - FORMAS 2 Y 3 DE IMPLEMENTACIÓN ====
        // Implementación con clase anónima y con expresión lambda sobre ReglaIncidencia.
        // =========================================================================================

        // 2) Clase anónima
        ReglaIncidencia abiertaAnonima = new ReglaIncidencia() {
            @Override
            public boolean evaluar(Incidencia incidencia) {
                return "ABIERTA".equals(incidencia.estado());
            }
        };

        // 3) Expresión lambda
        ReglaIncidencia abiertaLambda = incidencia -> "ABIERTA".equals(incidencia.estado());

        // =========================================================================================
        // ==== EN ESTE BLOQUE SE REALIZA: COMBINACIÓN Y ADAPTACIÓN A PREDICATE ====
        // Usa método default .y() y referencia a método de instancia requiereAtencion::evaluar.
        // =========================================================================================
        ReglaIncidencia requiereAtencion = abiertaLambda.y(new PrioridadAlta());
        Predicate<Incidencia> comoPredicado = requiereAtencion::evaluar;

        System.out.println("Regla anónima (ABIERTA) sobre el ejemplo: " + abiertaAnonima.evaluar(ejemplo));
        System.out.println("Regla lambda (ABIERTA) sobre el ejemplo: " + abiertaLambda.evaluar(ejemplo));
        System.out.println("Regla combinada y() sobre el ejemplo: " + requiereAtencion.evaluar(ejemplo));
        System.out.println("Regla adaptada a Predicate sobre el ejemplo: " + comoPredicado.test(ejemplo));

        // =========================================================================================
        // ==== EN ESTE BLOQUE SE REALIZA: AUTOVERIFICACIÓN DE LAS TRES FORMAS ====
        // Compara que la clase con nombre, la anónima y la lambda producen el mismo resultado.
        // =========================================================================================
        Incidencia abiertaUrgente = new Incidencia("INC-002", "Caída del sistema", "ABIERTA", 5);
        Incidencia cerradaBaja = new Incidencia("INC-003", "Cambio de contraseña", "CERRADA", 2);

        System.out.println("Autoverificación - ABIERTA con prioridad 5: " + abiertaUrgente);
        System.out.println("  abiertaAnonima.evaluar: " + abiertaAnonima.evaluar(abiertaUrgente));
        System.out.println("  abiertaLambda.evaluar:  " + abiertaLambda.evaluar(abiertaUrgente));
        System.out.println("  ¿Coinciden anónima y lambda? "
                + (abiertaAnonima.evaluar(abiertaUrgente) == abiertaLambda.evaluar(abiertaUrgente)));
        System.out.println("  new PrioridadAlta().evaluar: " + new PrioridadAlta().evaluar(abiertaUrgente));
        System.out.println("  requiereAtencion.evaluar: " + requiereAtencion.evaluar(abiertaUrgente));
        System.out.println("  comoPredicado.test: " + comoPredicado.test(abiertaUrgente));

        System.out.println("Autoverificación - CERRADA con prioridad 2: " + cerradaBaja);
        System.out.println("  abiertaAnonima.evaluar: " + abiertaAnonima.evaluar(cerradaBaja));
        System.out.println("  abiertaLambda.evaluar:  " + abiertaLambda.evaluar(cerradaBaja));
        System.out.println("  ¿Coinciden anónima y lambda? "
                + (abiertaAnonima.evaluar(cerradaBaja) == abiertaLambda.evaluar(cerradaBaja)));
        System.out.println("  new PrioridadAlta().evaluar: " + new PrioridadAlta().evaluar(cerradaBaja));
        System.out.println("  requiereAtencion.evaluar: " + requiereAtencion.evaluar(cerradaBaja));
        System.out.println("  comoPredicado.test: " + comoPredicado.test(cerradaBaja));

        // =========================================================================================
        // ==== FASE 4: DEMOSTRACIÓN DE REGLAS REUTILIZABLES ====
        // Ejecución de la función CATEGORIA, filtros y generación sintética con Supplier.
        // =========================================================================================
        System.out.println("CATEGORIA por prioridad:");
        for (int p = 1; p <= 5; p++) {
            Incidencia porPrioridad = new Incidencia("INC-P" + p, "Prioridad " + p, "ABIERTA", p);
            System.out.println("  prioridad " + p + " -> " + CATEGORIA.apply(porPrioridad));
        }

        Incidencia abiertaAlta = new Incidencia("INC-004", "Abierta prioridad 4", "ABIERTA", 4);
        Incidencia abiertaMedia = new Incidencia("INC-005", "Abierta prioridad 3", "ABIERTA", 3);

        System.out.println("Reglas sobre ABIERTA prioridad 4: " + abiertaAlta);
        System.out.println("  ESTA_ABIERTA: " + ESTA_ABIERTA.test(abiertaAlta));
        System.out.println("  PRIORIDAD_ALTA: " + PRIORIDAD_ALTA.test(abiertaAlta));
        System.out.println("  REQUIERE_ATENCION: " + REQUIERE_ATENCION.test(abiertaAlta));

        System.out.println("Reglas sobre ABIERTA prioridad 3: " + abiertaMedia);
        System.out.println("  ESTA_ABIERTA: " + ESTA_ABIERTA.test(abiertaMedia));
        System.out.println("  PRIORIDAD_ALTA: " + PRIORIDAD_ALTA.test(abiertaMedia));
        System.out.println("  REQUIERE_ATENCION: " + REQUIERE_ATENCION.test(abiertaMedia));

        List<Incidencia> sinteticas = Stream.generate(ReglasIncidencia.generadorSintetico()).limit(5).toList();
        System.out.println("5 incidencias generadas con Stream.generate:");
        sinteticas.forEach(System.out::println);

        System.out.println("NOTIFICAR sobre el ejemplo:");
        NOTIFICAR.accept(ejemplo);

        // =========================================================================================
        // ==== FASE 5: COMPOSICIÓN DE FUNCIONES Y REFERENCIAS A MÉTODOS ====
        // Demuestra composición de Predicate (negate, and, or), Function (andThen, compose) y Consumer.
        // =========================================================================================

        // Predicate: and, or, negate
        Predicate<Incidencia> noAbierta     = ESTA_ABIERTA.negate();
        // ==== FASE 5: SE COMPLETÓ EL TODO DE pendienteBaja CON AND Y NEGATE ====
        Predicate<Incidencia> pendienteBaja = ESTA_ABIERTA.and(PRIORIDAD_ALTA.negate()); // abiertas que NO son de prioridad alta
        Predicate<Incidencia> revisarHoy    = REQUIERE_ATENCION.or(i -> i.prioridad() == 5);

        // Function: andThen (primero this, luego la otra) y compose (primero la otra, luego this)
        Function<String, String> aMayusculas = String::toUpperCase;
        Function<Incidencia, String> resumenMayusculas = RESUMEN.andThen(aMayusculas);
        Function<Incidencia, String> mismoResultado    = aMayusculas.compose(RESUMEN);
        Function<Incidencia, String> etiqueta          = CATEGORIA.andThen(c -> "[" + c + "]");

        // Consumer: andThen encadena dos acciones sobre el mismo elemento
        List<String> bitacora = new ArrayList<>();
        Consumer<Incidencia> registrar = i -> bitacora.add(i.id());
        Consumer<Incidencia> notificarYRegistrar = NOTIFICAR.andThen(registrar);

        // Lista de incidencias de prueba para evaluar composición y referencias
        List<Incidencia> incidencias = List.of(
                new Incidencia("INC-006", "Crítica abierta", "ABIERTA", 5),
                new Incidencia("INC-007", "Abierta baja", "ABIERTA", 2),
                new Incidencia("INC-008", "Crítica cerrada", "CERRADA", 5),
                new Incidencia("INC-009", "En proceso alta", "EN_PROCESO", 4)
        );

        // =========================================================================================
        // ==== EN ESTE BLOQUE SE REALIZA: LAS CUATRO VARIANTES DE REFERENCIAS A MÉTODOS ====
        // 1. Estático, 2. Objeto particular, 3. Instancia arbitraria, 4. Constructor
        // =========================================================================================

        // 1. Método estático: Clase::metodoEstatico
        Predicate<Incidencia> esCritica = ReglasIncidencia::esCritica;

        System.out.println("Predicados sobre la lista de prueba:");
        for (Incidencia i : incidencias) {
            System.out.println("  " + i);
            System.out.println("    noAbierta: " + noAbierta.test(i));
            System.out.println("    pendienteBaja: " + pendienteBaja.test(i));
            System.out.println("    revisarHoy: " + revisarHoy.test(i));
            System.out.println("    esCritica: " + esCritica.test(i));
        }

        // 2. Método de un objeto particular: objeto::metodo (ejemplo: System.out::println)
        incidencias.forEach(System.out::println);

        // andThen vs compose sobre una misma incidencia para verificar equivalencia
        Incidencia paraFunciones = incidencias.get(0);
        String r1 = resumenMayusculas.apply(paraFunciones);
        String r2 = mismoResultado.apply(paraFunciones);
        System.out.println("resumenMayusculas: " + r1);
        System.out.println("mismoResultado:    " + r2);
        System.out.println("¿andThen y compose coinciden? " + r1.equals(r2));

        // Aplicación de función compuesta etiqueta (Function.andThen)
        System.out.println("etiqueta (prioridad 4): " + etiqueta.apply(incidencias.get(3)));

        // Aplicación del consumidor encadenado notificarYRegistrar (Consumer.andThen)
        System.out.println("notificarYRegistrar sobre REQUIERE_ATENCION:");
        incidencias.stream()
                .filter(REQUIERE_ATENCION)
                .forEach(notificarYRegistrar);
        System.out.println("bitacora: " + bitacora);

        // 3. Método de una instancia arbitraria: Tipo::metodoDeInstancia
        Function<Incidencia, String> titulo = Incidencia::titulo;
        System.out.println("titulo.apply: " + titulo.apply(paraFunciones));

        // 4. Constructor: Clase::new
        Supplier<List<Incidencia>> nuevaLista = ArrayList::new;
        FabricaIncidencia fabrica = Incidencia::new;
        List<Incidencia> creadas = nuevaLista.get();
        creadas.add(fabrica.crear("INC-100", "Creada con fábrica", "ABIERTA", 3));
        System.out.println("Lista creada con nuevaLista.get() y fabrica.crear: " + creadas);
    }
}
