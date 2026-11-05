package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("stagnantPriceResult")
class StagnantPriceResult implements JavaDelegate {

    @Override
    public void execute(DelegateExecution e) {

        // Recupero el último precio evaluado y su ronda.
        Number precio = (Number) e.getVariable("precioActual");
        Number ronda = (Number) e.getVariable("ronda");

        long ultimoPrecio = precio.longValue();
        int rondasEvaluadas = ronda.intValue();

        // El comprador no acepta y el vendedor no puede rebajar más.
        e.setVariable("estado", "SIN_ACUERDO");
        e.setVariable("ultimoPrecioCentimos", ultimoPrecio);
        e.setVariable("rondasEvaluadas", rondasEvaluadas);
        e.setVariable("motivo", "PRECIO_ESTANCADO");
    }
}