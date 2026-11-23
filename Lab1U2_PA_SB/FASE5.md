# Fase 5 — Composición de funciones y referencias a método

Proyecto: `ec.edu.espe.pa:lab21-funciones-bosquez:1.0-SNAPSHOT` (Java 21, Maven, sin Spring).

> Este archivo reemplaza a `FASE4.md` y documenta lo realizado en la Fase 5.

## Resumen de cambios

| Archivo | Estado | Acción |
|---|---|---|
| `src/main/java/ec/edu/espe/pa/funcional/reglas/ReglasIncidencia.java` | Modificado | Solo se agregó el método estático `esCritica` al final; nada más cambió |
| `src/main/java/ec/edu/espe/pa/funcional/contrato/FabricaIncidencia.java` | Nuevo | Interfaz funcional propia para el constructor de 4 parámetros |
| `src/main/java/ec/edu/espe/pa/funcional/Demostracion.java` | Modificado | Import de `FabricaIncidencia` + sección “Fase 5” en lugar del TODO |
| `src/main/java/ec/edu/espe/pa/funcional/imperativo/ProcesadorImperativo.java` | Intacto | No se tocó |
| `src/main/java/ec/edu/espe/pa/funcional/modelo/Incidencia.java` | Intacto | No se tocó |
| `src/main/java/ec/edu/espe/pa/funcional/contrato/ReglaIncidencia.java` | Intacto | No se tocó |
| `src/main/java/ec/edu/espe/pa/funcional/reglas/PrioridadAlta.java` | Intacto | No se tocó |
| `pom.xml`, `.gitignore`, `ProyectoConfiguradoTest.java` | Intactos | No se tocaron |

No se crearon archivos `.md` durante la implementación de la Fase 5; este documento se generó después, por solicitud explícita.

---

## 1. Método agregado en `ReglasIncidencia.java`

Se añadió al final de la clase, sin alterar el resto:

```java
    // Método estático usado como referencia a método (ReglasIncidencia::esCritica)
    public static boolean esCritica(Incidencia incidencia) {
        return incidencia.prioridad() == 5;
    }
```

## 2. Archivo nuevo: `FabricaIncidencia.java`

Ruta: `src/main/java/ec/edu/espe/pa/funcional/contrato/FabricaIncidencia.java`

```java
package ec.edu.espe.pa.funcional.contrato;

import ec.edu.espe.pa.funcional.modelo.Incidencia;

// Contrato propio para el constructor de cuatro parámetros:
// java.util.function no ofrece una interfaz estándar con cuatro entradas
@FunctionalInterface
public interface FabricaIncidencia {

    Incidencia crear(String id, String titulo, String estado, int prioridad);
}
```

Permite usar `Incidencia::new` como referencia a constructor, porque `java.util.function` no trae una interfaz estándar de cuatro entradas.

---

## 3. Modificación en `Demostracion.java`

### 3.1 Import añadido

```diff
+ import ec.edu.espe.pa.funcional.contrato.FabricaIncidencia;
  import ec.edu.espe.pa.funcional.contrato.ReglaIncidencia;
```

`ArrayList` y `List` ya estaban disponibles por `import java.util.*;`.

### 3.2 Sección “Fase 5” (reemplazó al `// TODO Fase 5:`)

```java
        // Fase 5: composición de funciones y referencias a método

        // Predicate: and, or, negate
        Predicate<Incidencia> noAbierta     = ESTA_ABIERTA.negate();
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

        // Lista de prueba (antes de las referencias a métodos)
        List<Incidencia> incidencias = List.of(
                new Incidencia("INC-006", "Crítica abierta", "ABIERTA", 5),
                new Incidencia("INC-007", "Abierta baja", "ABIERTA", 2),
                new Incidencia("INC-008", "Crítica cerrada", "CERRADA", 5),
                new Incidencia("INC-009", "En proceso alta", "EN_PROCESO", 4)
        );

        // Referencias a métodos, en sus cuatro variantes
        // Método estático: Clase::metodoEstatico
        Predicate<Incidencia> esCritica = ReglasIncidencia::esCritica;

        System.out.println("Predicados sobre la lista de prueba:");
        for (Incidencia i : incidencias) {
            System.out.println("  " + i);
            System.out.println("    noAbierta: " + noAbierta.test(i));
            System.out.println("    pendienteBaja: " + pendienteBaja.test(i));
            System.out.println("    revisarHoy: " + revisarHoy.test(i));
            System.out.println("    esCritica: " + esCritica.test(i));
        }

        // Método de un objeto particular: objeto::metodo
        incidencias.forEach(System.out::println);

        // andThen vs compose sobre una misma incidencia
        Incidencia paraFunciones = incidencias.get(0);
        String r1 = resumenMayusculas.apply(paraFunciones);
        String r2 = mismoResultado.apply(paraFunciones);
        System.out.println("resumenMayusculas: " + r1);
        System.out.println("mismoResultado:    " + r2);
        System.out.println("¿andThen y compose coinciden? " + r1.equals(r2));

        // etiqueta para una incidencia de prioridad 4
        System.out.println("etiqueta (prioridad 4): " + etiqueta.apply(incidencias.get(3)));

        // notificarYRegistrar solo sobre las que cumplen REQUIERE_ATENCION
        System.out.println("notificarYRegistrar sobre REQUIERE_ATENCION:");
        incidencias.stream()
                .filter(REQUIERE_ATENCION)
                .forEach(notificarYRegistrar);
        System.out.println("bitacora: " + bitacora);

        // Método de una instancia arbitraria: Tipo::metodoDeInstancia
        Function<Incidencia, String> titulo = Incidencia::titulo;
        System.out.println("titulo.apply: " + titulo.apply(paraFunciones));

        // Constructor: Clase::new
        Supplier<List<Incidencia>> nuevaLista = ArrayList::new;
        FabricaIncidencia fabrica = Incidencia::new;
        List<Incidencia> creadas = nuevaLista.get();
        creadas.add(fabrica.crear("INC-100", "Creada con fábrica", "ABIERTA", 3));
        System.out.println("Lista creada con nuevaLista.get() y fabrica.crear: " + creadas);
```

### 3.3 Contenido final de `Demostracion.java`

```java
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

public class Demostracion {

    public static void main(String[] args) {

        // 1. Las cuatro interfaces funcionales estándar
        Predicate<Incidencia> estaAbierta = incidencia -> "ABIERTA".equals(incidencia.estado());
        Function<Incidencia, String> obtenerResumen = incidencia -> incidencia.id() + " - " + incidencia.titulo();
        Consumer<Incidencia> mostrar = incidencia -> System.out.println(incidencia.titulo());
        Supplier<Incidencia> crearEjemplo = () -> new Incidencia("INC-001", "Error de acceso", "ABIERTA", 3);

        // 2. Invocación de las interfaces y impresión de cada resultado
        Incidencia ejemplo = crearEjemplo.get();
        boolean abierta = estaAbierta.test(ejemplo);
        String resumen = obtenerResumen.apply(ejemplo);

        System.out.println("Incidencia creada por el Supplier: " + ejemplo);
        System.out.println("¿Está abierta? (Predicate): " + abierta);
        System.out.println("Resumen (Function): " + resumen);
        System.out.println("Título impreso por el Consumer:");
        mostrar.accept(ejemplo);

        // 3. Sintaxis de lambdas
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

        // 4. Captura de variables efectivamente finales
        int umbral = 4;
        Predicate<Incidencia> superaUmbral = i -> i.prioridad() >= umbral;
        // umbral = 5; // Si se descomenta, no compila: "local variables referenced from a lambda expression must be final or effectively final"

        System.out.println("Captura de umbral (" + umbral + ") (prioridad >= umbral): " + superaUmbral.test(ejemplo));

        // Fase 3: formas de implementar ReglaIncidencia

        // 2) Clase anónima
        ReglaIncidencia abiertaAnonima = new ReglaIncidencia() {
            @Override
            public boolean evaluar(Incidencia incidencia) {
                return "ABIERTA".equals(incidencia.estado());
            }
        };

        // 3) Expresión lambda
        ReglaIncidencia abiertaLambda = incidencia -> "ABIERTA".equals(incidencia.estado());

        // Combinación con el método default y adaptación a Predicate mediante referencia a método
        ReglaIncidencia requiereAtencion = abiertaLambda.y(new PrioridadAlta());
        Predicate<Incidencia> comoPredicado = requiereAtencion::evaluar;

        System.out.println("Regla anónima (ABIERTA) sobre el ejemplo: " + abiertaAnonima.evaluar(ejemplo));
        System.out.println("Regla lambda (ABIERTA) sobre el ejemplo: " + abiertaLambda.evaluar(ejemplo));
        System.out.println("Regla combinada y() sobre el ejemplo: " + requiereAtencion.evaluar(ejemplo));
        System.out.println("Regla adaptada a Predicate sobre el ejemplo: " + comoPredicado.test(ejemplo));

        // Autoverificación con dos incidencias
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

        // Fase 4: reglas reutilizables en ReglasIncidencia
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

        // Fase 5: composición de funciones y referencias a método

        // Predicate: and, or, negate
        Predicate<Incidencia> noAbierta     = ESTA_ABIERTA.negate();
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

        // Lista de prueba (antes de las referencias a métodos)
        List<Incidencia> incidencias = List.of(
                new Incidencia("INC-006", "Crítica abierta", "ABIERTA", 5),
                new Incidencia("INC-007", "Abierta baja", "ABIERTA", 2),
                new Incidencia("INC-008", "Crítica cerrada", "CERRADA", 5),
                new Incidencia("INC-009", "En proceso alta", "EN_PROCESO", 4)
        );

        // Referencias a métodos, en sus cuatro variantes
        // Método estático: Clase::metodoEstatico
        Predicate<Incidencia> esCritica = ReglasIncidencia::esCritica;

        System.out.println("Predicados sobre la lista de prueba:");
        for (Incidencia i : incidencias) {
            System.out.println("  " + i);
            System.out.println("    noAbierta: " + noAbierta.test(i));
            System.out.println("    pendienteBaja: " + pendienteBaja.test(i));
            System.out.println("    revisarHoy: " + revisarHoy.test(i));
            System.out.println("    esCritica: " + esCritica.test(i));
        }

        // Método de un objeto particular: objeto::metodo
        incidencias.forEach(System.out::println);

        // andThen vs compose sobre una misma incidencia
        Incidencia paraFunciones = incidencias.get(0);
        String r1 = resumenMayusculas.apply(paraFunciones);
        String r2 = mismoResultado.apply(paraFunciones);
        System.out.println("resumenMayusculas: " + r1);
        System.out.println("mismoResultado:    " + r2);
        System.out.println("¿andThen y compose coinciden? " + r1.equals(r2));

        // etiqueta para una incidencia de prioridad 4
        System.out.println("etiqueta (prioridad 4): " + etiqueta.apply(incidencias.get(3)));

        // notificarYRegistrar solo sobre las que cumplen REQUIERE_ATENCION
        System.out.println("notificarYRegistrar sobre REQUIERE_ATENCION:");
        incidencias.stream()
                .filter(REQUIERE_ATENCION)
                .forEach(notificarYRegistrar);
        System.out.println("bitacora: " + bitacora);

        // Método de una instancia arbitraria: Tipo::metodoDeInstancia
        Function<Incidencia, String> titulo = Incidencia::titulo;
        System.out.println("titulo.apply: " + titulo.apply(paraFunciones));

        // Constructor: Clase::new
        Supplier<List<Incidencia>> nuevaLista = ArrayList::new;
        FabricaIncidencia fabrica = Incidencia::new;
        List<Incidencia> creadas = nuevaLista.get();
        creadas.add(fabrica.crear("INC-100", "Creada con fábrica", "ABIERTA", 3));
        System.out.println("Lista creada con nuevaLista.get() y fabrica.crear: " + creadas);
    }
}
```

---

## 4. Verificación realizada

Comandos ejecutados en la raíz del proyecto:

```bash
mvn compile
java -cp target/classes ec.edu.espe.pa.funcional.Demostracion
mvn test
```

Resultados:

| Comando | Resultado |
|---|---|
| `mvn compile` | `EXIT=0` (compila con `release 21`) |
| `java -cp target/classes ...` | Ejecuta sin errores |
| `mvn test` | `EXIT=0` (los 2 tests de la Fase 1 siguen pasando) |

## 5. Salida esperada de la sección Fase 5

Las secciones de Fase 1–4 se mantienen igual; lo nuevo es:

```text
Predicados sobre la lista de prueba:
  Incidencia[id=INC-006, titulo=Crítica abierta, estado=ABIERTA, prioridad=5]
    noAbierta: false
    pendienteBaja: false
    revisarHoy: true
    esCritica: true
  Incidencia[id=INC-007, titulo=Abierta baja, estado=ABIERTA, prioridad=2]
    noAbierta: false
    pendienteBaja: true
    revisarHoy: false
    esCritica: false
  Incidencia[id=INC-008, titulo=Crítica cerrada, estado=CERRADA, prioridad=5]
    noAbierta: true
    pendienteBaja: false
    revisarHoy: true
    esCritica: true
  Incidencia[id=INC-009, titulo=En proceso alta, estado=EN_PROCESO, prioridad=4]
    noAbierta: true
    pendienteBaja: false
    revisarHoy: false
    esCritica: false
Incidencia[id=INC-006, titulo=Crítica abierta, estado=ABIERTA, prioridad=5]
Incidencia[id=INC-007, titulo=Abierta baja, estado=ABIERTA, prioridad=2]
Incidencia[id=INC-008, titulo=Crítica cerrada, estado=CERRADA, prioridad=5]
Incidencia[id=INC-009, titulo=En proceso alta, estado=EN_PROCESO, prioridad=4]
resumenMayusculas: INC-006 - CRÍTICA ABIERTA
mismoResultado:    INC-006 - CRÍTICA ABIERTA
¿andThen y compose coinciden? true
etiqueta (prioridad 4): [ALTA]
notificarYRegistrar sobre REQUIERE_ATENCION:
[ATENCION] INC-006 - Crítica abierta
bitacora: [INC-006]
titulo.apply: Crítica abierta
Lista creada con nuevaLista.get() y fabrica.crear: [Incidencia[id=INC-100, titulo=Creada con fábrica, estado=ABIERTA, prioridad=3]]
```

## 6. Interpretación de los resultados

- **`negate` / `and` / `or`**:
  - `noAbierta` es true solo para `INC-008` (CERRADA) e `INC-009` (EN_PROCESO).
  - `pendienteBaja` es true solo para `INC-007` (ABIERTA y prioridad 2, es decir, abierta pero NO de prioridad alta).
  - `revisarHoy` es true para `INC-006` (cumple `REQUIERE_ATENCION`) y para `INC-008` (prioridad 5, aunque esté CERRADA, por el `or`).
- **`andThen` vs `compose`**: `RESUMEN.andThen(aMayusculas)` y `aMayusculas.compose(RESUMEN)` producen el mismo resultado (`true`), porque en ambos casos primero se calcula el resumen y luego se pasa a mayúsculas.
- **`etiqueta`**: `CATEGORIA.andThen(c -> "[" + c + "]")` sobre prioridad 4 produce `[ALTA]`.
- **`Consumer.andThen`**: `notificarYRegistrar` imprime la notificación y además registra el id en `bitacora`. Solo `INC-006` cumple `REQUIERE_ATENCION`, por eso la bitácora queda `[INC-006]`.
- **Referencias a método (4 variantes)**:
  1. Estático: `ReglasIncidencia::esCritica`
  2. Objeto particular: `System.out::println`
  3. Instancia arbitraria: `Incidencia::titulo`
  4. Constructor: `ArrayList::new` e `Incidencia::new` (este último a través de `FabricaIncidencia`).
- **`FabricaIncidencia`**: permite `Incidencia::new` pese a que el constructor tiene 4 parámetros y no existe interfaz estándar equivalente en `java.util.function`.

## 7. Notas

- El enunciado llegó cortado en `compos`; se completó con la parte posterior recibida (`compose`, `Consumer.andThen`, referencias a método y verificaciones).
- `exec-maven-plugin` sigue sin configurarse; se usa `java -cp target/classes ...`.
- Si en Windows se ven acentos mal en la consola, es codificación del terminal (`chcp 65001`), no del código: los archivos están en UTF-8.
- `FASE4.md` fue reemplazado por este `FASE5.md` según lo solicitado.
