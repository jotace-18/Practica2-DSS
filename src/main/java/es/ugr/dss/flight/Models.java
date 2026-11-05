package es.ugr.dss.flight;
import java.math.BigDecimal;
record TravelRequest(String employeeId, String destination, String departureDate, String returnDate) {}
record EmployeeInfo(boolean authorized, String travelClass) {}
record Quote(String airline, BigDecimal price, String currency, boolean available) {}
record TravelResult(String status, String employeeId, String travelClass, String selectedAirline, BigDecimal totalPrice, String currency, String detail) {}
