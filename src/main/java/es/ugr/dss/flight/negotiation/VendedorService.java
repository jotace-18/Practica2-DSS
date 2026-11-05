package es.ugr.dss.flight.negotiation;

import org.springframework.stereotype.Component;

@Component
class VendedorService {

    long calcularOferta(long precioActual, long precioMinimo) {
        // Sin indicar otro descuento, mantengo el 10 % del guion.
        return calcularOferta(precioActual, precioMinimo, 10);
    }

    long calcularOferta(long precioActual, long precioMinimo, int descuento) {
        if (descuento < 1 || descuento > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 1 y 100");
        }

        int porcentajeRestante = 100 - descuento;
        long precioNuevo = (precioActual * porcentajeRestante) / 100;

        // Aunque cambie el descuento, sigo respetando el mínimo.
        if (precioNuevo < precioMinimo) {
            precioNuevo = precioMinimo;
        }

        return precioNuevo;
    }
}
