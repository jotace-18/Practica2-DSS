package es.ugr.dss.flight;

import java.math.BigDecimal;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChooseOfferTest {

    @Test
    void samePriceChoosesIberia() {
        // Simulo las variables del proceso sin arrancar Flowable.
        DelegateExecution execution = mock(DelegateExecution.class);

        when(execution.getVariable("iberiaAvailable"))
            .thenReturn(true);

        when(execution.getVariable("vuelingAvailable"))
            .thenReturn(true);

        // Pongo el mismo precio con distinto número de decimales.
        BigDecimal precioIberia = new BigDecimal("200.00");
        BigDecimal precioVueling = new BigDecimal("200");

        when(execution.getVariable("iberiaPrice"))
            .thenReturn(precioIberia);

        when(execution.getVariable("vuelingPrice"))
            .thenReturn(precioVueling);

        // Las dos consultas han ido bien.
        when(execution.getVariable("iberiaFailed"))
            .thenReturn(false);
        when(execution.getVariable("vuelingFailed"))
            .thenReturn(false);

        ChooseOffer chooseOffer = new ChooseOffer();

        // Ahora ejecuto la selección con los datos que he preparado.
        chooseOffer.execute(execution);

        // En empate debe guardar Iberia y su precio.
        verify(execution).setVariable("status", "QUOTED");
        verify(execution).setVariable("selectedAirline", "IBERIA");
        verify(execution).setVariable("totalPrice", precioIberia);
    }

    @Test
    void noAvailableOffers() {
        DelegateExecution execution = mock(DelegateExecution.class);

        // Ninguna tiene vuelos y tampoco ha fallado la consulta.
        when(execution.getVariable("iberiaAvailable"))
            .thenReturn(false);
        when(execution.getVariable("vuelingAvailable"))
            .thenReturn(false);

        // Al no tener ofertas, los precios están vacíos.
        when(execution.getVariable("iberiaPrice"))
            .thenReturn(null);
        when(execution.getVariable("vuelingPrice"))
            .thenReturn(null);

        when(execution.getVariable("iberiaFailed"))
            .thenReturn(false);
        when(execution.getVariable("vuelingFailed"))
            .thenReturn(false);

        ChooseOffer chooseOffer = new ChooseOffer();

        chooseOffer.execute(execution);

        // Sin ofertas no debe elegir aerolínea ni guardar un precio.
        verify(execution).setVariable("status", "NO_OFFER");

        verify(execution).setVariable("selectedAirline", null);
        verify(execution).setVariable("totalPrice", null);
        verify(execution).setVariable("detail", "No hay ofertas disponibles");
    }
}
