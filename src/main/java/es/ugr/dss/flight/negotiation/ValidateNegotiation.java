package es.ugr.dss.flight.negotiation;

import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("validateNegotiation")
class ValidateNegotiation implements JavaDelegate {

    @Override
    public void execute(DelegateExecution e) {

        // Si falla la entrada, todavía no hemos evaluado ninguna oferta.
        e.setVariable("rondasEvaluadas", 0);

        Object presupuesto = e.getVariable("presupuestoCentimos");
        Object maximoRondas = e.getVariable("maxRondas");

        // Primero compruebo que los datos sean del tipo esperado.
        if (!(presupuesto instanceof Long) || !(maximoRondas instanceof Integer)) {
            throw new BpmnError("ENTRADA_INVALIDA");
        }

        long presupuestoCentimos = ((Number) presupuesto).longValue();
        int maxRondas = ((Number) maximoRondas).intValue();

        if (presupuestoCentimos <= 0) {
            throw new BpmnError("ENTRADA_INVALIDA");
        }

        if (maxRondas < 1 || maxRondas > 10) {
            throw new BpmnError("ENTRADA_INVALIDA");
        }
    }
}