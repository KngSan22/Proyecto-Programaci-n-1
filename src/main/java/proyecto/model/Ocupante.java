package proyecto.model;

import proyecto.exception.*;

import java.time.LocalDate;
import java.time.Period;

// Persona que se aloja
public class Ocupante {

    private String nombre;
    private LocalDate fechaNacimiento;

    public Ocupante(String nombre, LocalDate fechaNacimiento) throws PadreException {
        if (nombre == null || nombre.trim().equals("")) {
            throw new DatoInvalidoException("El nombre del ocupante es obligatorio.");
        }
        if (fechaNacimiento == null) {
            throw new DatoInvalidoException("La fecha de nacimiento del ocupante es obligatoria.");
        }
        this.nombre = nombre.trim();
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public int edadA(LocalDate fecha) {
        return Period.between(fechaNacimiento, fecha).getYears();
    }

    // Los menores de 6 años ocupan cupo pero no pagan
    public boolean paga(LocalDate fechaEntrada) {
        return edadA(fechaEntrada) >= 6;
    }
}