package es.ugr.dss.flight.negotiation;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.api.history.HistoricVariableInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

@Service
class NegotiationService {

    private final RuntimeService runtime;
    private final HistoryService history;

    NegotiationService(RuntimeService runtime, HistoryService history) {
        this.runtime = runtime;
        this.history = history;
    }

    NegotiationResult negociar(NegotiationRequest request) {

        // Preparo los datos que necesita el proceso.
        HashMap<String, Object> variables = new HashMap<String, Object>();

        variables.put("producto", request.producto());
        variables.put("presupuestoCentimos", request.presupuestoCentimos());
        variables.put("maxRondas", request.maxRondas());

        // Inicio el mismo BPMN que ya hemos probado con JUnit.
        ProcessInstance instance = runtime.startProcessInstanceByKey(
            "priceNegotiation",
            variables
        );

        String processId = instance.getId();

        // Como el proceso ha terminado, recupero sus variables del historial.
        HistoricVariableInstanceQuery consulta = history.createHistoricVariableInstanceQuery();
        consulta.processInstanceId(processId);

        List<HistoricVariableInstance> historicVariables = consulta.list();

        HashMap<String, Object> result = new HashMap<String, Object>();

        // Copio cada variable usando su nombre como clave.
        for (int i = 0; i < historicVariables.size(); i++) {
            HistoricVariableInstance variable = historicVariables.get(i);

            String nombre = variable.getVariableName();
            Object valor = variable.getValue();

            result.put(nombre, valor);
        }

        String estado = (String) result.get("estado");
        String motivo = (String) result.get("motivo");

        // Si hubo un error, puede que no exista un precio de salida.
        Long ultimoPrecio = null;
        Number precioGuardado = (Number) result.get("ultimoPrecioCentimos");

        if (precioGuardado != null) {
            ultimoPrecio = precioGuardado.longValue();
        }

        Number rondasGuardadas = (Number) result.get("rondasEvaluadas");
        int rondasEvaluadas = rondasGuardadas.intValue();

        // Recupero las ofertas en el orden en que las evaluó el comprador.
        ArrayList<Long> ofertas = new ArrayList<Long>();

        for (int ronda = 1; ronda <= rondasEvaluadas; ronda++) {
            String nombreOferta = "ofertaCentimos_" + ronda;
            Number ofertaGuardada = (Number) result.get(nombreOferta);

            long precioOferta = ofertaGuardada.longValue();
            ofertas.add(precioOferta);
        }

        NegotiationResult respuesta = new NegotiationResult(
            processId,
            estado,
            ultimoPrecio,
            rondasEvaluadas,
            motivo,
            ofertas
        );

        return respuesta;
    }
}
