package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("agreementResult")
class AgreementResult implements JavaDelegate {

    @Override
    public void execute(DelegateExecution e) {

        // Recupero el precio aceptado y la ronda en la que estamos.
        Number precio = (Number) e.getVariable("precioActual");
        Number ronda = (Number) e.getVariable("ronda");

        long precioAceptado = precio.longValue();
        int rondasEvaluadas = ronda.intValue();

        // Guardo el resultado final de la negociación.
        e.setVariable("estado", "ACUERDO");
        e.setVariable("ultimoPrecioCentimos", precioAceptado);
        e.setVariable("rondasEvaluadas", rondasEvaluadas);
        e.setVariable("motivo", null);
    }
}