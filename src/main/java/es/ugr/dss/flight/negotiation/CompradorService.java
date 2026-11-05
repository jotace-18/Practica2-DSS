package es.ugr.dss.flight.negotiation;
import org.springframework.stereotype.Component;

@Component
class CompradorService {

	boolean aceptaOferta(long precioActual, long precioInicial, long presupuesto) {
		// Aceptamos como máximo el 70% del precio inicial
		long limiteAceptacion = (70L * precioInicial)/100;
		
		// La oferta tiene que entrar en el presupuesto
		if (precioActual <= presupuesto && precioActual <= limiteAceptacion) {
			return true;
		}
		
		return false;
	}
}


