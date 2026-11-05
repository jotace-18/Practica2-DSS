# Práctica 2 — BPMN y Flowable

Proyecto realizado a partir del material del profesor para DSS. Incluye los procesos de reserva de vuelos y negociación de precios, sus pruebas y la memoria en PDF y Markdown.

## Entorno y ejecución

Java 21, Spring Boot 3.4.5, Flowable 7.2.0 y H2 en memoria. Las ejecuciones documentadas se hicieron en STS con Java 21.0.12.1.

Importar esta carpeta mediante **File > Import > Maven > Existing Maven Projects**. Ejecutar `es.ugr.dss.flight.FlightApplication`. El servidor usa el puerto 8080; `partners.base-url` debe apuntar al mismo puerto.

Con Maven 3.9+ instalado también puede ejecutarse:

```bash
mvn spring-boot:run
```

El proyecto no incluye Maven Wrapper. Los datos e historial de H2 se pierden al cerrar la JVM.

## Vuelos

```bash
curl -i -X POST http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  --data-binary @requests/authorized.json
```

Resultado: QUOTED, VUELING, 275 EUR. Otros archivos de `requests/`:

| Archivo | Resultado |
| --- | --- |
| rejected.json | REJECTED, sin aerolínea ni precio |
| no-offer.json | NO_OFFER, sin aerolínea ni precio |
| outage.json | QUOTED, IBERIA, 170 EUR; fallo de Vueling indicado |

Los servicios de empleados y aerolíneas se simulan en `/mock` y se consultan por HTTP. No se realizan reservas reales.

## Negociación y ampliaciones

```bash
curl -i -X POST http://localhost:8080/api/negotiations \
  -H 'Content-Type: application/json' \
  -d '{"producto":"CAMISA","presupuestoCentimos":7000,"maxRondas":6}'
```

Con el descuento del 10 % devuelve ACUERDO, 6561 céntimos y cinco rondas. Incluye `processInstanceId` y `ofertasCentimos`: `[10000,9000,8100,7290,6561]`.

El porcentaje se configura con `negotiation.descuento-porcentaje` en `src/main/resources/application.properties`. Está guardado al 10 para mantener los casos del guion. Tras cambiarlo hay que reiniciar. Con el 20 %, la petición anterior devuelve 6400 en tres rondas. Se permiten porcentajes de 1 a 100.

Los errores de negocio capturados se devuelven con HTTP 200 y `estado=ERROR`, motivo, cero rondas y precio null.

## Pruebas

Detener la aplicación antes de ejecutar las pruebas: las de vuelos levantan su propio servidor en 8080.

En STS: clic derecho en `src/test/java` > **Run As > JUnit Test**. Con Maven instalado:

```bash
mvn test
```

La ejecución conjunta documentada de la parte obligatoria pasó 24 pruebas. Las ampliaciones de API, historial y descuento configurable se comprobaron por terminal; la captura de las 24 pruebas es anterior a esos cambios.

## Modelos y memoria

Los modelos están en `src/main/resources/processes/`:

- `flight-booking.bpmn20.xml`: clave `flightBooking`.
- `price-negotiation.bpmn20.xml`: clave `priceNegotiation`.

Ambos conservan BPMN DI para abrirlos en bpmn.io. Tras exportar, comprobar las expresiones de Flowable, las condiciones y las referencias de errores antes de reiniciar.

La memoria se incluye como `Memoria_P2_DSS_JuanCarlosMontesRonda.pdf` y `.md`. Contiene diagramas, pruebas y resultados.

No incluir `target/`, archivos compilados, registros ni credenciales en el repositorio o el ZIP de entrega.
