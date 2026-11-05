package es.ugr.dss.flight.negotiation;

import java.util.List;

record NegotiationResult(
    String processInstanceId,
    String estado,
    Long ultimoPrecioCentimos,
    Integer rondasEvaluadas,
    String motivo,
    List<Long> ofertasCentimos
) {

}