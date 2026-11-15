package proyecto.model;

import proyecto.exception.PadreException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// El huesped reserva desde la aplicacion; queda Pendiente hasta pagar un anticipo del 30%
public class ReservaPortal extends Reserva {

    public ReservaPortal(String codigo, Apartamento apartamento, Huesped titular, ArrayList<Ocupante> ocupantes,
                         LocalDate entrada, LocalDate salida, LocalDateTime creadaEn, Cotizacion cotizacion,
                         int dias, int horas, int porcentaje) throws PadreException {
        super(codigo, apartamento, titular, ocupantes, entrada, salida, creadaEn, cotizacion, dias, horas, porcentaje);
    }

    public String getCanal() {
        return "PORTAL";
    }

    public long calcularAnticipo() {
        return getValorEstancia() * 30 / 100;
    }
}