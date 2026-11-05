package es.ugr.dss.flight.negotiation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VendedorServiceTest {

    @Test
    void rebajaUnDiezPorCiento() {
        VendedorService vendedor = new VendedorService();

        long precioActual = 10000L;
        long precioMinimo = 6000L;

        long oferta = vendedor.calcularOferta(precioActual, precioMinimo);

        assertThat(oferta).isEqualTo(9000L);
    }

    @Test
    void noBajaDelMinimo() {
        VendedorService vendedor = new VendedorService();

        long precioActual = 6561L;
        long precioMinimo = 6000L;

        // La rebaja daría 5904, así que debe quedarse en 6000.
        long oferta = vendedor.calcularOferta(precioActual, precioMinimo);

        assertThat(oferta).isEqualTo(6000L);
    }

    @Test
    void mantieneElPrecioSiYaEstaEnElMinimo() {
        VendedorService vendedor = new VendedorService();

        long precioActual = 6000L;
        long precioMinimo = 6000L;

        long oferta = vendedor.calcularOferta(precioActual, precioMinimo);

        assertThat(oferta).isEqualTo(6000L);
    }

    @Test
    void descartaLosDecimalesDeCentimo() {
        VendedorService vendedor = new VendedorService();

        // El 90 % de 6561 es 5904,9 céntimos: se descarta el decimal.
        // Uso otro mínimo para comprobar el cálculo sin que lo limite.
        long oferta = vendedor.calcularOferta(6561L, 5000L);

        assertThat(oferta).isEqualTo(5904L);
    }
}
