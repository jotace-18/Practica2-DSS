package es.ugr.dss.flight.negotiation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompradorServiceTest {

    @Test
    void aceptaSiCumpleLasDosCondiciones() {
        CompradorService comprador = new CompradorService();

        long precioInicial = 10000L;
        long precioActual = 7000L;
        long presupuesto = 7000L;

        // Está justo en el límite y puedo pagarlo.
        boolean acepta = comprador.aceptaOferta(
            precioActual,
            precioInicial,
            presupuesto
        );

        assertThat(acepta).isTrue();
    }

    @Test
    void rechazaSiNoLlegaElPresupuesto() {
        CompradorService comprador = new CompradorService();

        long precioInicial = 10000L;
        long precioActual = 7000L;
        long presupuesto = 6999L;

        // El precio cumple el descuento, pero me falta dinero.
        boolean acepta = comprador.aceptaOferta(
            precioActual,
            precioInicial,
            presupuesto
        );

        assertThat(acepta).isFalse();
    }

    @Test
    void rechazaSiNoTieneSuficienteDescuento() {
        CompradorService comprador = new CompradorService();

        long precioInicial = 10000L;
        long precioActual = 9000L;
        long presupuesto = 10000L;

        // Puedo pagarlo, pero supera el 70 % del precio inicial.
        boolean acepta = comprador.aceptaOferta(
            precioActual,
            precioInicial,
            presupuesto
        );

        assertThat(acepta).isFalse();
    }
}