package proyecto.model;

import proyecto.exception.*;
public class Administrador extends Usuario {

    public Administrador(String nombre, String documento, String usuario, String clave) throws PadreException {
        super(nombre, documento, usuario, clave);
    }

    public String getRol() {
        return "ADMINISTRADOR";
    }
}