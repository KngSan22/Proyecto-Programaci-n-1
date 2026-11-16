package proyecto.viewController;

import javafx.scene.control.*;

import proyecto.exception.*;
import proyecto.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

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
            throw new DatoInvalidoException("Número invalido: '" + texto + "'. Escriba solo dígitos, sin puntos.");
        }
    }

    // Cada línea del texto es un ocupante
    public static ArrayList<Ocupante> ocupantes(String texto) throws PadreException {
        ArrayList<Ocupante> lista = new ArrayList<Ocupante>();
        String[] lineas = texto.split("\n");
        for (String linea : lineas) {
            if (linea.trim().equals("")) {
                continue;
            }
            String[] partes = linea.split(",");
            if (partes.length != 2) {
                throw new DatoInvalidoException("Cada acompañante debe escribirse asi: Nombre, aaaa-mm-dd");
            }
            lista.add(new Ocupante(partes[0], fecha(partes[1])));
        }
        return lista;
    }

    public static void mensaje(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    public static void error(Exception e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText("No se pudo realizar la operación");
        if (e instanceof PadreException) {
            alerta.setContentText(e.getMessage());
        } else {
            alerta.setContentText("Error: " + e);
        }
        alerta.showAndWait();
    }

    // Pregunta y devuelve true si el usuario acepta
    public static boolean confirmar(String texto) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        return alerta.showAndWait().get() == ButtonType.OK;
    }
}