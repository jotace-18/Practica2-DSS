package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("maxRoundsResult")
class MaxRoundsResult implements JavaDelegate {

    @Override
    public void execute(DelegateExecution e) {

        // Recupero la última oferta que se ha evaluado.
        Number precio = (Number) e.getVariable("precioActual");
        Number ronda = (Number) e.getVariable("ronda");

        long ultimoPrecio = precio.longValue();
        int rondasEvaluadas = ronda.intValue();

        // Se han agotado las rondas sin que el comprador acepte.
        e.setVariable("estado", "SIN_ACUERDO");
        e.setVariable("ultimoPrecioCentimos", ultimoPrecio);
        e.setVariable("rondasEvaluadas", rondasEvaluadas);
        e.setVariable("motivo", "MAX_RONDAS");
    }
}