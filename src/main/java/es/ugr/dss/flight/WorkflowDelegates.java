package es.ugr.dss.flight;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("checkEmployee") class CheckEmployee implements JavaDelegate {
 private final HttpPartners partners; CheckEmployee(HttpPartners partners){this.partners=partners;}
 @Override public void execute(DelegateExecution e){
  try {var info=partners.employee((String)e.getVariable("employeeId"));e.setVariable("authorized",info.authorized());e.setVariable("travelClass",info.travelClass());}
  catch(Exception ex){e.setVariable("authorized",false);e.setVariable("travelClass","NONE");}
 }
}

@Component("rejectEmployee") class RejectEmployee implements JavaDelegate {
 @Override public void execute(DelegateExecution e){e.setVariable("status","REJECTED");e.setVariable("detail","Employee not authorized or unknown");}
}
@Component("quoteIberia") class QuoteIberia implements JavaDelegate {
 private final HttpPartners partners;QuoteIberia(HttpPartners partners){this.partners=partners;}
 @Override public void execute(DelegateExecution e){
  var r=new TravelRequest((String)e.getVariable("employeeId"),(String)e.getVariable("destination"),(String)e.getVariable("departureDate"),(String)e.getVariable("returnDate"));
  try {
   var q = partners.quote("IBERIA", r, (String) e.getVariable("travelClass"));

   e.setVariable("iberiaAvailable", q.available());
   e.setVariable("iberiaPrice", q.price());
   e.setVariable("iberiaFailed", false);
  } catch (org.springframework.web.client.RestClientException ex) {
   e.setVariable("iberiaAvailable", false);
   e.setVariable("iberiaPrice", null);
   e.setVariable("iberiaFailed", true);
  }
 }
}

// Este nombre es para enlazar la clase desde el BPMN.
@Component("quoteVueling")
class QuoteVueling implements JavaDelegate {

    private final HttpPartners partners;

    QuoteVueling(HttpPartners partners) {
        // Spring me pasa el cliente para hacer las consultas HTTP.
        this.partners = partners;
    }

    @Override
    public void execute(DelegateExecution e) {
        // Recupero los datos que ya tiene guardados el proceso.
        String employeeId = (String) e.getVariable("employeeId");
        String destination = (String) e.getVariable("destination");
        String departureDate = (String) e.getVariable("departureDate");
        String returnDate = (String) e.getVariable("returnDate");
        String travelClass = (String) e.getVariable("travelClass");

        // Agrupo los datos para consultar la oferta.
        TravelRequest request = new TravelRequest(
            employeeId,
            destination,
            departureDate,
            returnDate
        );

        try {
            // Consulto Vueling y guardo su oferta para compararla después.
            Quote quote = partners.quote("VUELING", request, travelClass);

            e.setVariable("vuelingAvailable", quote.available());
            e.setVariable("vuelingPrice", quote.price());
            e.setVariable("vuelingFailed", false);
        } catch (org.springframework.web.client.RestClientException ex) {
            // Si falla Vueling, dejo que el proceso siga con la otra aerolínea.
            e.setVariable("vuelingAvailable", false);
            e.setVariable("vuelingPrice", null);
            e.setVariable("vuelingFailed", true);
        }
    }
}

@Component("chooseOffer")
class ChooseOffer implements JavaDelegate {

    @Override
    public void execute(DelegateExecution e) {
        // Recupero la disponibilidad y el precio de cada aerolínea.
        boolean iberiaAvailable = Boolean.TRUE.equals(e.getVariable("iberiaAvailable"));
        boolean vuelingAvailable = Boolean.TRUE.equals(e.getVariable("vuelingAvailable"));

        BigDecimal iberiaPrice = (BigDecimal) e.getVariable("iberiaPrice");
        BigDecimal vuelingPrice = (BigDecimal) e.getVariable("vuelingPrice");

        // Miro si alguna consulta ha fallado.
        boolean iberiaFailed = Boolean.TRUE.equals(e.getVariable("iberiaFailed"));
        boolean vuelingFailed = Boolean.TRUE.equals(e.getVariable("vuelingFailed"));

        // Una oferta solo sirve si está disponible y tiene precio.
        boolean iberiaValid = iberiaAvailable && iberiaPrice != null;
        boolean vuelingValid = vuelingAvailable && vuelingPrice != null;

        String selectedAirline = null;
        BigDecimal totalPrice = null;

        if (iberiaValid && vuelingValid) {
            // Si cuestan lo mismo, me quedo con Iberia.
            if (iberiaPrice.compareTo(vuelingPrice) <= 0) {
                selectedAirline = "IBERIA";
                totalPrice = iberiaPrice;
            } 
            else {
                selectedAirline = "VUELING";
                totalPrice = vuelingPrice;
            }
        } 
        
        else if (iberiaValid) {
            selectedAirline = "IBERIA";
            totalPrice = iberiaPrice;
        } 
        
        else if (vuelingValid) {
            selectedAirline = "VUELING";
            totalPrice = vuelingPrice;
        }

        String status;
        String detail;

        if (selectedAirline != null) {
            status = "QUOTED";
            detail = "Oferta seleccionada de " + selectedAirline;
        } else {
            status = "NO_OFFER";
            detail = "No hay ofertas disponibles";
        }

        // Dejo indicado si faltan ofertas por un fallo de consulta.
        if (iberiaFailed) {
            detail = detail + ". Fallo al consultar Iberia";
        }

        if (vuelingFailed) {
            detail = detail + ". Fallo al consultar Vueling";
        }

        // Guardo el resultado final que devolverá el controlador.
        e.setVariable("status", status);
        e.setVariable("selectedAirline", selectedAirline);
        e.setVariable("totalPrice", totalPrice);
        e.setVariable("detail", detail);
    }
}





