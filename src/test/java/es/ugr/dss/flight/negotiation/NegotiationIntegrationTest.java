package es.ugr.dss.flight.negotiation;
import org.flowable.engine.history.HistoricVariableUpdate;
import java.util.HashMap;
import java.util.Map;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NegotiationIntegrationTest {
    @Autowired RuntimeService runtime;
    @Autowired HistoryService history;

    private Map<String,Object> execute(String product, long budget, int maxRounds) {
        var vars=new HashMap<String,Object>();
        vars.put("producto", product);
        vars.put("presupuestoCentimos", budget);
        vars.put("maxRondas", maxRounds);
        var instance=runtime.startProcessInstanceByKey("priceNegotiation",vars);
        var result=new HashMap<String,Object>();
        history.createHistoricVariableInstanceQuery()
               .processInstanceId(instance.getId()).list()
               .forEach(v -> result.put(v.getVariableName(),v.getValue()));
        		System.out.println("\n===== NEGOTIATION RESULT =====");
        		System.out.println("Process instance: " + instance.getId());
        		result.forEach((name, value) ->
        		System.out.println(name + " = " + value)
        		);
        		System.out.println("==============================\n");

        		System.out.println("===== BPMN ACTIVITIES =====");

        		history.createHistoricActivityInstanceQuery()
        		.processInstanceId(instance.getId())
        		.orderByHistoricActivityInstanceStartTime()
        		.asc()
        		.list()
        		.forEach(a -> System.out.println(
        				a.getActivityId() + " -> " +
        						a.getActivityName()
        				));

        assertNotNull(history.createHistoricProcessInstanceQuery()
            .processInstanceId(instance.getId()).finished().singleResult());
        return result;
    }

    private void check(String product,long budget,int rounds,String state,Long price,int evaluated,String reason) {
        var result=execute(product,budget,rounds);
        assertEquals(state,result.get("estado"));
        assertEquals(evaluated,((Number)result.get("rondasEvaluadas")).intValue());
        if (price==null) assertNull(result.get("ultimoPrecioCentimos"));
        else assertEquals(price.longValue(),((Number)result.get("ultimoPrecioCentimos")).longValue());
        assertEquals(reason,result.get("motivo"));
    }

    @Test void shirtExamples() {
        check("CAMISA",10000,6,"ACUERDO",6561L,5,null);
        check("CAMISA",7000,6,"ACUERDO",6561L,5,null);
        check("CAMISA",7000,4,"SIN_ACUERDO",7290L,4,"MAX_RONDAS");
        check("CAMISA",7000,1,"SIN_ACUERDO",10000L,1,"MAX_RONDAS");
        check("CAMISA",6000,10,"ACUERDO",6000L,6,null);
        check("CAMISA",5900,10,"SIN_ACUERDO",6000L,6,"PRECIO_ESTANCADO");
    }
    @Test void suitAndErrors() {
        check("TRAJE",20000,10,"SIN_ACUERDO",16000L,4,"PRECIO_ESTANCADO");
        check("ZAPATOS",10000,6,"ERROR",null,0,"SIN_EXISTENCIAS");
        check("LIBRO",10000,6,"ERROR",null,0,"PRODUCTO_DESCONOCIDO");
        check("CAMISA",7000,11,"ERROR",null,0,"ENTRADA_INVALIDA");
    }
    @Test void boundaryCases() {
        check("CAMISA",7000,5,"ACUERDO",6561L,5,null);
        check("CAMISA",5900,6,"SIN_ACUERDO",6000L,6,"MAX_RONDAS");
    }

@Test
void demonstrateNegotiation() {

    System.out.println(
        "\n===== DEMOSTRACION DE REGATEO =====");

    Map<String, Object> result =
        execute("CAMISA", 7000L, 6);

    System.out.println("\n===== RESULTADO FINAL =====");

    System.out.println(
        "Estado: " + result.get("estado"));

    System.out.println(
        "Precio final (centimos): " +
        result.get("ultimoPrecioCentimos"));

    System.out.println(
        "Rondas evaluadas: " +
        result.get("rondasEvaluadas"));

    assertEquals("ACUERDO", result.get("estado"));
    assertEquals(6561L,
        ((Number) result.get(
            "ultimoPrecioCentimos")).longValue());

    assertEquals(5,
        ((Number) result.get(
            "rondasEvaluadas")).intValue());
}

@Test
void showBuyerExecutionHistory() {

    // 1. Variables de entrada
    var vars = new HashMap<String, Object>();
    vars.put("producto", "CAMISA");
    vars.put("presupuestoCentimos", 7000L);
    vars.put("maxRondas", 6);

    // 2. Ejecutar el proceso completo
    var instance = runtime.startProcessInstanceByKey(
        "priceNegotiation", vars);

    String processId = instance.getId();

    System.out.println(
        "\n===== HISTORIAL REAL FLOWABLE =====");
    System.out.println("Instancia: " + processId);

    // 3. Consultar las ejecuciones de evaluateBuyer
    var activities = history
        .createHistoricActivityInstanceQuery()
        .processInstanceId(processId)
        .activityId("buyer")
        .orderByHistoricActivityInstanceStartTime()
        .asc()
        .list();

    System.out.println(
        "Ejecuciones de evaluateBuyer: "
        + activities.size());

    int round = 1;

    for (var activity : activities) {
        System.out.printf(
            "Ronda %d: actividad=%s, id=%s%n",
            round++,
            activity.getActivityId(),
            activity.getId());
    }

System.out.println(
    "\n===== EVOLUCION DEL PRECIO =====");

var details = history
    .createHistoricDetailQuery()
    .processInstanceId(processId)
    .variableUpdates()
    .orderByTime()
    .asc()
    .list();

int priceRound = 0;

for (var detail : details) {

    if (detail instanceof HistoricVariableUpdate update
        && "precioActual".equals(
            update.getVariableName())) {

        long price = ((Number)
            update.getValue()).longValue();

        priceRound++;

        System.out.printf(
            "Precio registrado %d: %d centimos "
            + "(%.2f EUR)%n",
            priceRound,
            price,
            price / 100.0
        );
    }
}

    // 4. Verificar que se ejecutó cinco veces
    assertEquals(5, activities.size());
}


}
