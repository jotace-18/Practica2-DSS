package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("outOfStockResult")
class OutOfStockResult implements JavaDelegate {

    @Override
    public void execute(DelegateExecution e) {

        // El producto existe, pero no quedan unidades.
        e.setVariable("estado", "ERROR");
        e.setVariable("motivo", "SIN_EXISTENCIAS");
        e.setVariable("rondasEvaluadas", 0);

        // No se ha llegado a negociar ningún precio.
        e.removeVariable("ultimoPrecioCentimos");
    }
}
