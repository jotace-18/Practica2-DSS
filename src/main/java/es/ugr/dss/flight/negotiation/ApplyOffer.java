package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("applyOffer")
class ApplyOffer implements JavaDelegate {

    @Override
    public void execute(DelegateExecution e) {

        // Recupero la nueva oferta y la ronda actual.
        Number nuevo = (Number) e.getVariable("precioNuevo");
        Number rondaGuardada = (Number) e.getVariable("ronda");

        long precioNuevo = nuevo.longValue();
        int ronda = rondaGuardada.intValue();

        // La oferta nueva pasa a ser la que evaluará el comprador.
        e.setVariable("precioActual", precioNuevo);

        // Como vamos a evaluar otra oferta, avanzamos una ronda.
        ronda = ronda + 1;
        e.setVariable("ronda", ronda);
    }
}
