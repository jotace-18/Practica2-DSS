package es.ugr.dss.flight.negotiation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/negotiations")
class NegotiationController {

    private final NegotiationService service;

    NegotiationController(NegotiationService service) {
        this.service = service;
    }

    @PostMapping
    NegotiationResult negociar(@RequestBody NegotiationRequest request) {

        // Paso los datos al servicio para que ejecute el regateo.
        NegotiationResult result = service.negociar(request);

        // Spring convierte la respuesta a JSON.
        return result;
    }
}
