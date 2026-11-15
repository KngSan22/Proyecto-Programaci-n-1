package proyecto.model;

import proyecto.exception.PadreException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Se le paga el 15% de comision
public class ReservaExterna extends Reserva {

    private String codigoExterno;

    public ReservaExterna(String codigo, String codigoExterno, Apartamento apartamento, Huesped titular,
                          ArrayList<Ocupante> ocupantes, LocalDate entrada, LocalDate salida, LocalDateTime creadaEn,
                          Cotizacion cotizacion, int dias, int horas, int porcentaje) throws PadreException {
        super(codigo, apartamento, titular, ocupantes, entrada, salida, creadaEn, cotizacion, dias, horas, porcentaje);
        this.codigoExterno = codigoExterno;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }

    public String getCanal() {
        return "EXTERNA";
    }

    public long calcularAnticipo() {
        return 0;
    }

    public long calcularComision() {
        return getValorEstancia() * 15 / 100;
    }
}