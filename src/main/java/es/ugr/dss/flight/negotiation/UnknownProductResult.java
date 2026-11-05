package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("unknownProductResult")
class UnknownProductResult implements JavaDelegate {

    @Override
    public void execute(DelegateExecution e) {

        // El producto no aparece en el catálogo.
        e.setVariable("estado", "ERROR");
        e.setVariable("motivo", "PRODUCTO_DESCONOCIDO");
        e.setVariable("rondasEvaluadas", 0);

        // No se ha llegado a negociar ningún precio.
        e.removeVariable("ultimoPrecioCentimos");
    }
}