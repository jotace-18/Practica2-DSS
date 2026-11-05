
package es.ugr.dss.flight.negotiation;

// Los datos del regateo
record NegotiationRequest(
    String producto,
    Long presupuestoCentimos,
    Integer maxRondas
) {

}