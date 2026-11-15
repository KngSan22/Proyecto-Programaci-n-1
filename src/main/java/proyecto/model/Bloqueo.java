package proyecto.model;

import uniquindio.edu.co.sga.exception.DatoInvalidoException;
import uniquindio.edu.co.sga.exception.PadreException;

import java.time.LocalDate;

// Rango de noches (incluidas las dos fechas) en que un apartamento no se puede vender
public class Bloqueo {

    private Apartamento apartamento;
    private LocalDate desde;
    private LocalDate hasta;
    private String motivo;

    public Bloqueo(Apartamento apartamento, LocalDate desde, LocalDate hasta, String motivo) throws PadreException {
        if (hasta.isBefore(desde)) {
            throw new DatoInvalidoException("La fecha final del bloqueo no puede ser anterior a la inicial.");
        }
        if (motivo == null || motivo.trim().equals("")) {
            throw new DatoInvalidoException("El motivo del bloqueo es obligatorio.");
        }
        this.apartamento = apartamento;
        this.desde = desde;
        this.hasta = hasta;
        this.motivo = motivo.trim();
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public LocalDate getDesde() {
        return desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public String getMotivo() {
        return motivo;
    }

    // Una estancia [entrada, salida) choca si alguna de sus noches está dentro del bloqueo
    public boolean chocaCon(LocalDate entrada, LocalDate salida) {
        return desde.isBefore(salida) && !hasta.isBefore(entrada);
    }

    public String toString() {
        return apartamento.getCodigo() + " bloqueado del " + desde + " al " + hasta + " (" + motivo + ")";
    }
}