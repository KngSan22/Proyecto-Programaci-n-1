package proyecto.model;

// Resultado de cancelar, cuanto se retiene y cuanto se devuelve
public class Liquidacion {

    private long valorEstancia;
    private long pagado;
    private int porcentajeRetenido;
    private long retenido;
    private long devolucion;

    public Liquidacion(long valorEstancia, long pagado, int porcentajeRetenido, long retenido, long devolucion) {
        this.valorEstancia = valorEstancia;
        this.pagado = pagado;
        this.porcentajeRetenido = porcentajeRetenido;
        this.retenido = retenido;
        this.devolucion = devolucion;
    }
}
