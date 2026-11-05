package es.ugr.dss.flight.negotiation;

import org.springframework.stereotype.Component;

@Component
class CatalogoService {
	Producto buscarProducto(String nombre) {
		if ("CAMISA".equals(nombre)) {
			return new Producto(10, 10000L, 6000L);
		}
		
		if ("TRAJE".equals(nombre)) {
			return new Producto(3, 20000L, 16000L);
		}
		
		if ("ZAPATOS".equals(nombre)) {
			return new Producto(0, 8000L, 5000L);
		}
		
		// Si no aparece en el catálogo, retornamos null
		return null;
	}

}
