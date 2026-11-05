package es.ugr.dss.flight;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
@Component class HttpPartners {
 private final RestClient client;
 HttpPartners(@Value("${partners.base-url:http://localhost:8080}") String url){this.client=RestClient.builder().baseUrl(url).build();}
 EmployeeInfo employee(String id){return client.get().uri("/mock/employees/{id}",id).retrieve().body(EmployeeInfo.class);}
 Quote quote(String airline,TravelRequest r,String cls){
  return client.get().uri(b->b.path("/mock/airlines/{airline}/quote").queryParam("destination",r.destination()).queryParam("travelClass",cls).queryParam("departureDate",r.departureDate()).queryParam("returnDate",r.returnDate()).build(airline)).retrieve().body(Quote.class);
 }
}
