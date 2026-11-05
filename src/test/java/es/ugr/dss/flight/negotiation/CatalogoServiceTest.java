package es.ugr.dss.flight.negotiation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogoServiceTest {

    @Test
    void encuentraLaCamisa() {
        CatalogoService catalogo = new CatalogoService();

        Producto producto = catalogo.buscarProducto("CAMISA");

        assertThat(producto).isNotNull();
        assertThat(producto.stock()).isEqualTo(10);
        assertThat(producto.precioInicial()).isEqualTo(10000L);
        assertThat(producto.precioMinimo()).isEqualTo(6000L);
    }

    @Test
    void encuentraElTraje() {
        CatalogoService catalogo = new CatalogoService();

        Producto producto = catalogo.buscarProducto("TRAJE");

        assertThat(producto).isNotNull();
        assertThat(producto.stock()).isEqualTo(3);
        assertThat(producto.precioInicial()).isEqualTo(20000L);
        assertThat(producto.precioMinimo()).isEqualTo(16000L);
    }

    @Test
    void zapatosExistePeroNoTieneStock() {
        CatalogoService catalogo = new CatalogoService();

        Producto producto = catalogo.buscarProducto("ZAPATOS");

        // Existe en el catálogo, aunque no quedan unidades.
        assertThat(producto).isNotNull();
        assertThat(producto.stock()).isEqualTo(0);
        assertThat(producto.precioInicial()).isEqualTo(8000L);
        assertThat(producto.precioMinimo()).isEqualTo(5000L);
    }

    @Test
    void productoDesconocidoDevuelveNull() {
        CatalogoService catalogo = new CatalogoService();

        Producto producto = catalogo.buscarProducto("LIBRO");

        // LIBRO no aparece en nuestro catálogo.
        assertThat(producto).isNull();
    }
}