package proyecto.exception;

// Exception base del sistema
public class PadreException extends Exception {
    public PadreException(String mensaje) {
        super(mensaje);
    }
}