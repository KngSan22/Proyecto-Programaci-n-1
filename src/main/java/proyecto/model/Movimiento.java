package proyecto.model;

import java.time.LocalDateTime;

// Una linea del folio, puede ser un cargo (algo que se debe) o un pago (algo que se abona)
// Una vez creada no se cambia nunca, una corrección guarda cuál movimiento corrige
public class Movimiento {

    private String concepto;
    private long valor;
    private LocalDateTime fecha;
    private MedioPago medio;
    private Movimiento corrige;

    public Movimiento(String concepto, long valor, LocalDateTime fecha, MedioPago medio) {
        this(concepto, valor, fecha, medio, null);
    }

    public Movimiento(String concepto, long valor, LocalDateTime fecha, MedioPago medio, Movimiento corrige) {
        this.concepto = concepto;
        this.valor = valor;
        this.fecha = fecha;
        this.medio = medio;
        this.corrige = corrige;
    }

    public String getConcepto() {
        return concepto;
    }

    public long getValor() {
        return valor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public MedioPago getMedio() {
        return medio;
    }

    public Movimiento getCorrige() {
        return corrige;
    }

    public String toString() {
        String texto = fecha.toLocalDate() + " | " + concepto + " | " + Formato.dinero(valor);
        if (medio != null) {
            texto = texto + " (" + medio + ")";
        }
        return texto;
    }
}