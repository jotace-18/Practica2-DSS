package es.ugr.dss.flight;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
@RestController @RequestMapping("/mock")
class ProvidersController {
 @GetMapping("/employees/{id}") EmployeeInfo employee(@PathVariable String id) {
  return switch(id) {case "EMP001" -> new EmployeeInfo(true,"ECONOMY");case "EMP002" -> new EmployeeInfo(true,"BUSINESS");case "EMP999" -> new EmployeeInfo(false,"NONE");default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Unknown employee");};
 }
 @GetMapping("/airlines/{airline}/quote") Quote quote(@PathVariable String airline,@RequestParam String destination,@RequestParam String travelClass,@RequestParam String departureDate,@RequestParam String returnDate) {
  if (!airline.equals("IBERIA") && !airline.equals("VUELING")) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  LocalDate start,end;
  try {start=LocalDate.parse(departureDate);end=LocalDate.parse(returnDate);} catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid ISO dates");}
  if(end.isBefore(start)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Return before departure");
  if(destination.equals("NONE")) return new Quote(airline,null,"EUR",false);
  if(destination.equals("FAIL") && airline.equals("VUELING")) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Simulated outage");
  int base=switch(destination){case "AMS" -> 250;case "FCO" -> 180;default -> 120;};
  int surcharge=airline.equals("IBERIA")?50:25;
  int multiplier=travelClass.equals("BUSINESS")?2:1;
  return new Quote(airline,BigDecimal.valueOf((base+surcharge)*multiplier),"EUR",true);
 }
}
