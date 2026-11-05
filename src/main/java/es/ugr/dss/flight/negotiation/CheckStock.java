package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkStock")
class CheckStock implements JavaDelegate {

    private final CatalogoService catalogo;

    CheckStock(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @Override
    public void execute(DelegateExecution e) {

        String nombre = (String) e.getVariable("producto");
        Producto producto = catalogo.buscarProducto(nombre);

        // Primero miro si el producto existe.
        if (producto == null) {
            throw new BpmnError("PRODUCTO_DESCONOCIDO");
        }

        // Si existe, compruebo que queden unidades.
        if (producto.stock() <= 0) {
            throw new BpmnError("SIN_EXISTENCIAS");
        }

        // Guardo los precios que usaremos durante el regateo.
        e.setVariable("precioInicial", producto.precioInicial());
        e.setVariable("precioMinimo", producto.precioMinimo());
    }
}