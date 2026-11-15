package proyecto.model;

import proyecto.exception.PadreException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// La registra la recepción (teléfono, mensajería o presencial) no exige anticipo
public class ReservaDirecta extends Reserva {

    public ReservaDirecta(String codigo, Apartamento apartamento, Huesped titular, ArrayList<Ocupante> ocupantes,
                          LocalDate entrada, LocalDate salida, LocalDateTime creadaEn, Cotizacion cotizacion,
                          int dias, int horas, int porcentaje) throws PadreException {
        super(codigo, apartamento, titular, ocupantes, entrada, salida, creadaEn, cotizacion, dias, horas, porcentaje);
    }

    public String getCanal() {
        return "DIRECTA";
    }

    public long calcularAnticipo() {
        return 0;
    }
}