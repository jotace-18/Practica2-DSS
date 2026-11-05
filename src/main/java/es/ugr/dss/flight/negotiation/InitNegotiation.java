package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("initNegotiation")
class InitNegotiation implements JavaDelegate {

    @Override
    public void execute(DelegateExecution e) {

        // Recupero el precio inicial que guardó CheckStock.
        Number precioGuardado = (Number) e.getVariable("precioInicial");
        long precioInicial = precioGuardado.longValue();

        // La primera oferta es el precio inicial del producto.
        e.setVariable("precioActual", precioInicial);

        // La primera oferta se evalúa en la ronda 1.
        e.setVariable("ronda", 1);
    }
}