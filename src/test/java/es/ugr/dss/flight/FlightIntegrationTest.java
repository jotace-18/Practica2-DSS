package es.ugr.dss.flight;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.flowable.engine.HistoryService;
import java.util.ArrayList;
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT)
class FlightIntegrationTest {
 @Autowired TestRestTemplate http;
 @Autowired
 HistoryService history;

 private ResponseEntity<TravelResult> book(String emp, String dest, String start, String end) {
     // Guardo los IDs que ya existían para encontrar la instancia de esta petición.
     var previousIds = new ArrayList<String>();
     var previousProcesses = history.createHistoricProcessInstanceQuery()
         .processDefinitionKey("flightBooking").list();

     for (var process : previousProcesses) {
         previousIds.add(process.getId());
     }

     TravelRequest request = new TravelRequest(emp, dest, start, end);
     var response = http.postForEntity(
         "http://localhost:8080/api/bookings", request, TravelResult.class);

     System.out.println("\n--- PRUEBA DE VUELOS ---");
     System.out.println("Entrada: " + request);
     System.out.println("HTTP: " + response.getStatusCode());
     System.out.println("Resultado: " + response.getBody());

     var processes = history.createHistoricProcessInstanceQuery()
         .processDefinitionKey("flightBooking").list();

     int newProcesses = 0;
     for (var process : processes) {
         if (!previousIds.contains(process.getId())) {
             newProcesses++;
             String processId = process.getId();
             System.out.println("processInstanceId: " + processId);
             assertThat(process.getEndTime()).isNotNull();

             var activities = history.createHistoricActivityInstanceQuery()
                 .processInstanceId(processId)
                 .orderByHistoricActivityInstanceStartTime().asc().list();

             var serviceTasks = new ArrayList<String>();
             for (var activity : activities) {
                 System.out.println("Actividad: " + activity.getActivityId()
                     + " -> " + activity.getActivityName());

                 if ("serviceTask".equals(activity.getActivityType())) {
                     serviceTasks.add(activity.getActivityId());
                 }
             }

             // Para un rechazo solo deben ejecutarse la comprobación y el rechazo.
             if (response.getBody() != null
                     && "REJECTED".equals(response.getBody().status())) {
                 assertThat(serviceTasks).containsExactlyInAnyOrder("checkEmployee", "reject");
                 System.out.println("Comprobado: rechazo sin tareas de cotización.");
             }

             // Muestro también las ofertas y el resultado que quedaron guardados.
             var variables = history.createHistoricVariableInstanceQuery()
                 .processInstanceId(processId).list();

             for (var variable : variables) {
                 System.out.println(variable.getVariableName() + " = " + variable.getValue());
             }
         }
     }

     if (response.getStatusCode() == HttpStatus.OK) {
         assertThat(newProcesses).isEqualTo(1);
     }

     return response;
 }
 @Test
 void authorizedGetsQuote() {
     // Envío una solicitud de un empleado autorizado.
     var response = book("EMP001", "AMS", "2026-11-10", "2026-11-14");

     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
     var result = response.getBody();
     assertThat(result).isNotNull();

     // Compruebo la clase y la oferta que se ha elegido.
     assertThat(result.status()).isEqualTo("QUOTED");
     assertThat(result.travelClass()).isEqualTo("ECONOMY");
     assertThat(result.selectedAirline()).isEqualTo("VUELING");

     assertThat(result.totalPrice()).isEqualByComparingTo("275");
 }
 
 @Test
 void vuelingFailureKeepsIberiaOffer() {
     // Con este destino falla Vueling, pero Iberia sigue teniendo oferta.
     var response = book("EMP001", "FAIL", "2026-11-10", "2026-11-14");

     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

     var result = response.getBody();
     assertThat(result).isNotNull();
     assertThat(result.status()).isEqualTo("QUOTED");

     // Compruebo que no se pierde la oferta de Iberia.
     assertThat(result.selectedAirline()).isEqualTo("IBERIA");
     assertThat(result.totalPrice()).isEqualByComparingTo("170");
     assertThat(result.detail()).contains("Fallo al consultar Vueling");
 }

 @Test
 void businessEmployeeGetsQuote() {
     // EMP002 tiene autorizada la clase BUSINESS.
     var response = book("EMP002", "AMS", "2026-11-10", "2026-11-14");

     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

     var result = response.getBody();
     assertThat(result).isNotNull();
     assertThat(result.status()).isEqualTo("QUOTED");

     // Compruebo que se mantiene la clase y se aplica su precio.
     assertThat(result.travelClass()).isEqualTo("BUSINESS");
     assertThat(result.selectedAirline()).isEqualTo("VUELING");
     assertThat(result.totalPrice()).isEqualByComparingTo("550");
 }

 @Test
 void unauthorizedIsRejected() {
     // Este empleado no tiene permiso para viajar.
     var response = book("EMP999", "AMS", "2026-11-10", "2026-11-14");

     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
     var result = response.getBody();
     assertThat(result).isNotNull();

     assertThat(result.status()).isEqualTo("REJECTED");

     // Al rechazar la solicitud no debe haber aerolínea ni precio.
     assertThat(result.selectedAirline()).isNull();
     assertThat(result.totalPrice()).isNull();
 }

 @Test
 void invalidDatesRejected() {
     long processesBefore = history.createHistoricProcessInstanceQuery()
         .processDefinitionKey("flightBooking").count();

     // El regreso es anterior a la salida.
     var response = book("EMP001", "AMS", "2026-11-14", "2026-11-10");
     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

     long processesAfter = history.createHistoricProcessInstanceQuery()
         .processDefinitionKey("flightBooking").count();

     // No basta con el 400: tampoco debe haberse creado un proceso.
     assertThat(processesAfter).isEqualTo(processesBefore);
     System.out.println("Comprobado: fechas inválidas sin iniciar proceso.");
 }
 @Test
 void noOffers() {
     // Para NONE ninguna de las dos aerolíneas tiene vuelos.
     var response = book("EMP001", "NONE", "2026-11-10", "2026-11-14");

     assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

     var result = response.getBody();
     assertThat(result).isNotNull();
     assertThat(result.status()).isEqualTo("NO_OFFER");

     // Sin ofertas, estos datos deben quedarse vacíos.
     assertThat(result.selectedAirline()).isNull();
     assertThat(result.totalPrice()).isNull();
 }
}
