package ec.edu.espe.pa.funcional.contrato;

import ec.edu.espe.pa.funcional.modelo.Incidencia;

@FunctionalInterface
public interface ReglaIncidencia {

    boolean evaluar(Incidencia incidencia);
    



    // Un método default no rompe el contrato: sigue existiendo un solo método abstracto
    default ReglaIncidencia y(ReglaIncidencia otra) {
        return incidencia -> this.evaluar(incidencia) && otra.evaluar(incidencia);
    }
}
