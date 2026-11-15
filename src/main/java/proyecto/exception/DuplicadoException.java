package proyecto.exception;

// Se intenta registrar algo que ya está
public class DuplicadoException extends PadreException {
    public DuplicadoException(String mensaje) {
        super(mensaje);
    }
}