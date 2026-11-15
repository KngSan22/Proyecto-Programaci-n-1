package proyecto.model;

import proyecto.exception.*;

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
}
