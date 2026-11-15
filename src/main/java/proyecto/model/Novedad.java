package proyecto.model;

import proyecto.exception.*;

import java.time.LocalDateTime;

// Daño, faltante o situación reportada en un apartamento
public class Novedad {

    private Apartamento apartamento;
    private String descripcion;
    private LocalDateTime fecha;

    public Novedad(Apartamento apartamento, String descripcion, LocalDateTime fecha) throws PadreException {
        if (descripcion == null || descripcion.trim().equals("")) {
            throw new DatoInvalidoException("La descripcion de la novedad es obligatoria.");
        }
        this.apartamento = apartamento;
        this.descripcion = descripcion.trim();
        this.fecha = fecha;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public String toString() {
        return fecha.toLocalDate() + " | " + apartamento.getCodigo() + " | " + descripcion;
    }
}