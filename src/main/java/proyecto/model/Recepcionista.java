package proyecto.model;

import uniquindio.edu.co.sga.exception.PadreException;
public class Recepcionista extends Usuario {

    private String telefono;
    private String correo;
    private boolean activo = true;

    public Recepcionista(String nombre, String documento, String telefono, String correo, String usuario, String clave) throws PadreException {
        super(nombre, documento, usuario, clave);
        this.telefono = telefono;
        this.correo = correo;
    }

    public String getRol() {
        return "RECEPCIONISTA";
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void editarDatos(String nombre, String telefono, String correo) throws PadreException {
        setNombre(nombre);
        this.telefono = telefono;
        this.correo = correo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void desvincular() {
        activo = false;
    }

    public String toString() {
        String texto = getNombre() + " (" + getDocumento() + ") usuario: " + getUsuario();
        if (!activo) {
            texto = texto + " [DESVINCULADO]";
        }
        return texto;
    }
}