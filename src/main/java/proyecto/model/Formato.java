package proyecto.model;

public class Formato {

    // 1250000 -> $1.250.000
    public static String dinero(long valor) {
        String texto = String.format("%,d", valor);
        texto = texto.replace(",", ".");
        return "$" + texto;
    }
}