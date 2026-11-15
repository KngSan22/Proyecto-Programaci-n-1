package proyecto.model;

import uniquindio.edu.co.sga.exception.*;

import java.time.LocalDate;

// Rango de fechas (incluidas las dos) de una temporada MEDIA o ALTA
public class PeriodoTemporada {

    private Temporada temporada;
    private LocalDate desde;
    private LocalDate hasta;

    public PeriodoTemporada(Temporada temporada, LocalDate desde, LocalDate hasta) throws PadreException {
        if (temporada == Temporada.BAJA) {
            throw new DatoInvalidoException("La temporada baja es la base y no se define por fechas.");
        }
        if (hasta.isBefore(desde)) {
            throw new DatoInvalidoException("La fecha final no puede ser anterior a la inicial.");
        }
        this.temporada = temporada;
        this.desde = desde;
        this.hasta = hasta;
    }

    public Temporada getTemporada() {
        return temporada;
    }

    public boolean contiene(LocalDate fecha) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    public String toString() {
        return temporada + ": " + desde + " hasta " + hasta;
    }
}