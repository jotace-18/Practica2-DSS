package es.ugr.dss.flight;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.HistoryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController @RequestMapping("/api/bookings")
class BookingController {
 private final RuntimeService runtime; 
 private final HistoryService history;
 
 BookingController(RuntimeService runtime,HistoryService history){
	 this.runtime=runtime;this.history=history;
	 }
 @PostMapping TravelResult book(@RequestBody TravelRequest r){
  if(r.employeeId()==null||r.employeeId().isBlank()||
		  r.destination()==null||r.destination().isBlank()) 
	  throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing fields");
  try {if(LocalDate.parse(r.returnDate()).isBefore(LocalDate.parse(r.departureDate()))) 
	  throw new IllegalArgumentException();
  } 
  catch(Exception e){
	    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid dates");
  }
  var vars=new HashMap<String,Object>();
  vars.put("employeeId",r.employeeId());
  vars.put("destination",r.destination());
  vars.put("departureDate",r.departureDate());
  vars.put("returnDate",r.returnDate());
  
  var pi=runtime.startProcessInstanceByKey("flightBooking",vars);
  var h=history.createHistoricVariableInstanceQuery().processInstanceId(pi.getId()).list();
  Map<String,Object> result=new HashMap<>();for(var v:h)result.put(v.getVariableName(),v.getValue());
 
  return new TravelResult((String)result.get("status"),r.employeeId(),(String)result.get("travelClass"),(String)result.get("selectedAirline"),(BigDecimal)result.get("totalPrice"),"EUR",(String)result.get("detail"));
 }
}
