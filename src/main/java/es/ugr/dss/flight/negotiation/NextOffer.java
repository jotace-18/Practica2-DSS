package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component("nextOffer")
class NextOffer implements JavaDelegate {

    private final VendedorService vendedor;

    private final int descuento;

    NextOffer(VendedorService vendedor,
              @Value("${negotiation.descuento-porcentaje:10}") int descuento) {

        // Si la configuración está mal, lo aviso al arrancar.
        if (descuento < 1 || descuento > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 1 y 100");
        }

        this.vendedor = vendedor;
        this.descuento = descuento;
    }

    @Override
    public void execute(DelegateExecution e) {

        // Recupero el precio actual y el mínimo del vendedor.
        Number actual = (Number) e.getVariable("precioActual");
        Number minimo = (Number) e.getVariable("precioMinimo");

        long precioActual = actual.longValue();
        long precioMinimo = minimo.longValue();

        // Calculo la siguiente oferta con la regla que ya probamos.
        long precioNuevo = vendedor.calcularOferta(
            precioActual,
            precioMinimo,
            descuento
        );

        e.setVariable("precioNuevo", precioNuevo);
    }
}