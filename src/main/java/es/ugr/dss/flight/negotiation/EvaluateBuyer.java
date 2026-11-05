package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.DelegateExecution;

import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("evaluateBuyer")
class EvaluateBuyer implements JavaDelegate {

    private final CompradorService comprador;

    EvaluateBuyer(CompradorService comprador) {
        this.comprador = comprador;
    }

    @Override
    public void execute(DelegateExecution e) {

        // Recupero los precios y el presupuesto del proceso.
        Number actual = (Number) e.getVariable("precioActual");
        Number inicial = (Number) e.getVariable("precioInicial");
        Number presupuesto = (Number) e.getVariable("presupuestoCentimos");

        long precioActual = actual.longValue();
        long precioInicial = inicial.longValue();
        long presupuestoCentimos = presupuesto.longValue();

        // Recupero la ronda para identificar la oferta que estoy evaluando.
        Number rondaGuardada = (Number) e.getVariable("ronda");
        int ronda = rondaGuardada.intValue();

        // Guardo cada precio por separado para recuperarlos después en orden.
        e.setVariable("ofertaCentimos_" + ronda, precioActual);

        // El servicio comprueba si el comprador acepta esta oferta.
        boolean acepta = comprador.aceptaOferta(
            precioActual,
            precioInicial,
            presupuestoCentimos
        );

        // El BPMN leerá esta variable para elegir el camino.
        e.setVariable("aceptable", acepta);
    }
}
