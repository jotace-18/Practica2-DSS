package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("invalidInputResult")
class InvalidInputResult implements JavaDelegate {

    @Override
    public void execute(DelegateExecution e) {

        // La entrada no permite empezar la negociación.
        e.setVariable("estado", "ERROR");
        e.setVariable("motivo", "ENTRADA_INVALIDA");
        e.setVariable("rondasEvaluadas", 0);

        // No hay ningún precio negociado válido.
        e.removeVariable("ultimoPrecioCentimos");
    }
}