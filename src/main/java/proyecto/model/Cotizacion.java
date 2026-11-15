package proyecto.model;

import java.util.ArrayList;

// Valor de una estancia con su desglose noche por noche
public class Cotizacion {

    private long total = 0;
    private int noches = 0;
    private int personasQuePagan;
    private int totalOcupantes;
    private ArrayList<String> desglose = new ArrayList<String>();

    public Cotizacion(int personasQuePagan, int totalOcupantes) {
        this.personasQuePagan = personasQuePagan;
        this.totalOcupantes = totalOcupantes;
    }

    public void agregarNoche(String texto, long valor) {
        desglose.add(texto);
        total = total + valor;
        noches = noches + 1;
    }

    public long getTotal() {
        return total;
    }

    public int getNoches() {
        return noches;
    }

    public int getPersonasQuePagan() {
        return personasQuePagan;
    }

    public ArrayList<String> getDesglose() {
        return desglose;
    }

    public String resumen() {
        String texto = "";
        for (String linea : desglose) {
            texto = texto + linea + "\n";
        }
        texto = texto + "\n" + noches + " noches, " + personasQuePagan + " de " + totalOcupantes + " personas pagan.";
        texto = texto + "\nTOTAL: " + Formato.dinero(total);
        return texto;
    }
}
