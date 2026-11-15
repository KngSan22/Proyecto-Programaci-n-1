package proyecto.model;

import uniquindio.edu.co.sga.exception.*;

import java.time.LocalDateTime;

public class Apartamento {

    private String codigo;
    private String nombre;
    private int dormitorios;
    private int capacidad;
    private String caracteristicas;
    private long tarifaBaja;
    private long tarifaMedia;
    private long tarifaAlta;
    private EstadoApartamento estado = EstadoApartamento.PREPARADO;
    private String motivoFueraDeServicio = "";
    private boolean activo = true;
    private LocalDateTime ultimaSalida = null;

    public Apartamento(String codigo, String nombre, int dormitorios, int capacidad, String caracteristicas, long tarifaBaja, long tarifaMedia, long tarifaAlta) throws PadreException {
        if (codigo == null || codigo.trim().equals("")) {
            throw new DatoInvalidoException("El código del apartamento es obligatorio.");
        }
        this.codigo = codigo.trim();
        modificar(nombre, dormitorios, capacidad, caracteristicas);
        definirTarifas(tarifaBaja, tarifaMedia, tarifaAlta);
    }

    public void modificar(String nombre, int dormitorios, int capacidad, String caracteristicas) throws PadreException {
        if (nombre == null || nombre.trim().equals("")) {
            throw new DatoInvalidoException("El nombre del apartamento es obligatorio.");
        }
        if (dormitorios < 1) {
            throw new DatoInvalidoException("Debe tener al menos 1 dormitorio.");
        }
        if (capacidad < 1) {
            throw new DatoInvalidoException("La capacidad debe ser de al menos 1 persona.");
        }
        this.nombre = nombre.trim();
        this.dormitorios = dormitorios;
        this.capacidad = capacidad;
        this.caracteristicas = caracteristicas;
    }

    public void definirTarifas(long baja, long media, long alta) throws PadreException {
        if (baja <= 0 || media <= 0 || alta <= 0) {
            throw new DatoInvalidoException("Las tarifas deben ser mayores que cero.");
        }
        this.tarifaBaja = baja;
        this.tarifaMedia = media;
        this.tarifaAlta = alta;
    }

    public long tarifaDe(Temporada temporada) {
        if (temporada == Temporada.ALTA) {
            return tarifaAlta;
        }
        if (temporada == Temporada.MEDIA) {
            return tarifaMedia;
        }
        return tarifaBaja;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDormitorios() {
        return dormitorios;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public EstadoApartamento getEstado() {
        return estado;
    }

    public String getMotivoFueraDeServicio() {
        return motivoFueraDeServicio;
    }

    // Si pasa a FUERA_DE_SERVICIO hay que registrar el motivo
    public void setEstado(EstadoApartamento nuevo, String motivo) throws PadreException {
        if (nuevo == EstadoApartamento.FUERA_DE_SERVICIO) {
            if (motivo == null || motivo.trim().equals("")) {
                throw new ReglaNegocioException("Debe indicar el motivo para dejar el apartamento fuera de servicio.");
            }
            motivoFueraDeServicio = motivo.trim();
        } else {
            motivoFueraDeServicio = "";
        }
        estado = nuevo;
    }

    // Check-out: queda pendiente de preparacion y se anota la hora de salida
    public void registrarSalida(LocalDateTime ahora) throws PadreException {
        setEstado(EstadoApartamento.PENDIENTE_PREPARACION, "");
        ultimaSalida = ahora;
    }

    // true si desde la ultima salida ya pasaron las horas de preparación
    public boolean preparacionCumplida(LocalDateTime ahora, int horas) {
        return ultimaSalida == null || !ahora.isBefore(ultimaSalida.plusHours(horas));
    }

    public boolean isActivo() {
        return activo;
    }

    // Eliminación lógica, no se borra, solo se marca
    public void eliminar() {
        activo = false;
    }

    public String toString() {
        return codigo + " - " + nombre + " (capacidad " + capacidad + ", " + dormitorios + " dormitorio) ["
                + estado + "] baja " + Formato.dinero(tarifaBaja) + " / media " + Formato.dinero(tarifaMedia)
                + " / alta " + Formato.dinero(tarifaAlta);
    }
}