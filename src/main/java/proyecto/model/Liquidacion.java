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

    public long getValorEstancia() {
        return valorEstancia;
    }

    public long getPagado() {
        return pagado;
    }

    public int getPorcentajeRetenido() {
        return porcentajeRetenido;
    }

    public long getRetenido() {
        return retenido;
    }

    public long getDevolucion() {
        return devolucion;
    }

    public String resumen() {
        return "Valor de la estancia: " + Formato.dinero(valorEstancia)
                + "\nPagado: " + Formato.dinero(pagado)
                + "\nSe retiene (" + porcentajeRetenido + "%): " + Formato.dinero(retenido)
                + "\nSe devuelve: " + Formato.dinero(devolucion);
    }
}