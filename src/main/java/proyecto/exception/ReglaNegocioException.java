package proyecto.exception;

// No se cumple una regla del negocio
public class ReglaNegocioException extends PadreException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}