package proyecto.model;

import uniquindio.edu.co.sga.exception.*;

import java.time.LocalDate;

public class Huesped extends Usuario {

    private LocalDate fechaNacimiento;
    private String telefono;
    private String correo;

    public Huesped(String nombre, String documento, LocalDate fechaNacimiento, String telefono, String correo, String usuario, String clave) throws PadreException {
        super(nombre, documento, usuario, clave);
        if (fechaNacimiento == null) {
            throw new DatoInvalidoException("La fecha de nacimiento es obligatoria.");
        }
        if (correo == null || !correo.contains("@")) {
            throw new DatoInvalidoException("El correo no es valido.");
        }
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.correo = correo;
    }

    public String getRol() {
        return "HUESPED";
    }

    // El huesped puede cambiar su nombre, telefono y correo
    public void editarDatos(String nombre, String telefono, String correo) throws PadreException {
        if (correo == null || !correo.contains("@")) {
            throw new DatoInvalidoException("El correo no es valido.");
        }
        setNombre(nombre);
        this.telefono = telefono;
        this.correo = correo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }
}