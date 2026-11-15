package proyecto.model;

import proyecto.exception.*;

// Clase padre de todos los que inician sesión
public abstract class Usuario {

    private String nombre;
    private String documento;
    private String usuario;
    private String clave;

    public Usuario(String nombre, String documento, String usuario, String clave) throws PadreException {
        if (nombre == null || nombre.trim().equals("")) {
            throw new DatoInvalidoException("El nombre es obligatorio.");
        }
        if (documento == null || documento.trim().equals("")) {
            throw new DatoInvalidoException("El documento es obligatorio.");
        }
        if (usuario == null || usuario.trim().equals("")) {
            throw new DatoInvalidoException("El usuario es obligatorio.");
        }
        if (clave == null || clave.length() < 4) {
            throw new DatoInvalidoException("La clave debe tener al menos 4 caracteres.");
        }
        this.nombre = nombre.trim();
        this.documento = documento.trim();
        this.usuario = usuario.trim();
        this.clave = clave;
    }

    // Cada hijo dice cuál es su rol
    public abstract String getRol();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) throws PadreException {
        if (nombre == null || nombre.trim().equals("")) {
            throw new DatoInvalidoException("El nombre es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public String getDocumento() {
        return documento;
    }

    public String getUsuario() {
        return usuario;
    }

    public boolean claveCorrecta(String intento) {
        return clave.equals(intento);
    }

    public String toString() {
        return nombre + " (" + documento + ")";
    }
}