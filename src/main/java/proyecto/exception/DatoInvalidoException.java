package proyecto.exception;

// Un dato falta o tiene un formato/valor no permitido
public class DatoInvalidoException extends PadreException {
    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}