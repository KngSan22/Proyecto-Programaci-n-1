package proyecto.model;

import proyecto.exception.PadreException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

// Reserva de un apartamento completo
public abstract class Reserva {

    // Salida a las 11 am, entrada a las 3 pm y mínimo 3 horas de preparacion entre una y otra
    public static final LocalTime HORA_ENTRADA = LocalTime.of(15, 0);
    public static final int HORAS_PREPARACION = 3;

    private String codigo;
    private Apartamento apartamento;
    private Huesped titular;
    private ArrayList<Ocupante> ocupantes;
    private LocalDate entrada;
    private LocalDate salida;
    private LocalDateTime creadaEn;
    private long valorEstancia;
    private EstadoReserva estado = EstadoReserva.PENDIENTE;
    private Folio folio = new Folio();
    private Liquidacion liquidacion = null;

    // La politica de cancelación se copia aqui para que un cambio posterior no afecte esta reserva
    private int politicaDias;
    private int politicaHoras;
    private int politicaPorcentaje;

    public Reserva(String codigo, Apartamento apartamento, Huesped titular, ArrayList<Ocupante> ocupantes, LocalDate entrada,
                   LocalDate salida, LocalDateTime creadaEn, Cotizacion cotizacion, int politicaDias, int politicaHoras,
                   int politicaPorcentaje) throws PadreException {
        this.codigo = codigo;
        this.apartamento = apartamento;
        this.titular = titular;
        this.ocupantes = ocupantes;
        this.entrada = entrada;
        this.salida = salida;
        this.creadaEn = creadaEn;
        this.valorEstancia = cotizacion.getTotal();
        this.politicaDias = politicaDias;
        this.politicaHoras = politicaHoras;
        this.politicaPorcentaje = politicaPorcentaje;
        if (valorEstancia > 0) {
            folio.agregarCargo("Alojamiento " + apartamento.getCodigo() + " del " + entrada + " al " + salida, valorEstancia, creadaEn);
        }
    }

    // Lo que cambia según el canal

    public abstract String getCanal();

    public abstract long calcularAnticipo();

    public long calcularComision() {
        return 0;
    }

    // Consultas

    public String getCodigo() {
        return codigo;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public Huesped getTitular() {
        return titular;
    }

    public ArrayList<Ocupante> getOcupantes() {
        return new ArrayList<Ocupante>(ocupantes);
    }

    public LocalDate getEntrada() {
        return entrada;
    }

    public LocalDate getSalida() {
        return salida;
    }

    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }

    public long getValorEstancia() {
        return valorEstancia;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public Folio getFolio() {
        return folio;
    }

    public Liquidacion getLiquidacion() {
        return liquidacion;
    }

    // Activa = Pendiente, Confirmada o En curso
    public boolean estaActiva() {
        return estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA || estado == EstadoReserva.EN_CURSO;
    }

    // Noches realmente vendidas
    public boolean generaOcupacion() {
        return estado == EstadoReserva.CONFIRMADA || estado == EstadoReserva.EN_CURSO || estado == EstadoReserva.FINALIZADA;
    }

    // La noche de la fecha de salida no se cobra ni se ocupa
    public boolean ocupaNoche(LocalDate noche) {
        return !noche.isBefore(entrada) && noche.isBefore(salida);
    }

    // Dos estancias comparten noche si una empieza antes de que termine la otra
    public boolean chocaCon(LocalDate otraEntrada, LocalDate otraSalida) {
        return entrada.isBefore(otraSalida) && otraEntrada.isBefore(salida);
    }
}
