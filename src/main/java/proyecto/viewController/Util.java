package proyecto.viewController;

import proyecto.exception.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// Metodos de ayuda para los controladores
public class Util {

    public static LocalDate fecha(String texto) throws DatoInvalidoException {
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException("Fecha invalida: '" + texto + "'. Use el formato aaaa-mm-dd (ejemplo 2026-12-20).");
        }
    }

    public static long numero(String texto) throws DatoInvalidoException {
        try {
            return Long.parseLong(texto.trim());
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("Numero invalido: '" + texto + "'. Escriba solo digitos, sin puntos.");
        }
    }
}