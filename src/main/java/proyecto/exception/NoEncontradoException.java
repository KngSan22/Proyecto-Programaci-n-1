package proyecto.exception;

// No existe lo que se busca
public class NoEncontradoException extends PadreException {
    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}