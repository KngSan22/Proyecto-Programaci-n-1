package proyecto.model;

import proyecto.exception.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

// Reserva de un apartamento completo
public abstract class Reserva implements Cancelable {

    // Salida a las 11 am, entrada a las 3 pm y mínimo 3 horas de preparación entre una y otra
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

    // Politica de cancelación
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

    public boolean anticipoCubierto() {
        return folio.totalPagos() >= calcularAnticipo();
    }

    public boolean plazoVencido(LocalDateTime ahora) {
        return estado == EstadoReserva.PENDIENTE && !ahora.isBefore(creadaEn.plusHours(24));
    }

    // Cambios de estado
    // Revisa la tabla de estados permitidos
    private boolean transicionPermitida(EstadoReserva nuevo) {
        if (estado == EstadoReserva.PENDIENTE) {
            return nuevo == EstadoReserva.CONFIRMADA || nuevo == EstadoReserva.CANCELADA;
        }
        if (estado == EstadoReserva.CONFIRMADA) {
            return nuevo == EstadoReserva.EN_CURSO || nuevo == EstadoReserva.CANCELADA || nuevo == EstadoReserva.NO_SHOW;
        }
        if (estado == EstadoReserva.EN_CURSO) {
            return nuevo == EstadoReserva.FINALIZADA;
        }
        return false; // Finalizada, Cancelada y No-show
    }

    private void exigirTransicion(EstadoReserva nuevo) throws PadreException {
        if (!transicionPermitida(nuevo)) {
            throw new ReglaNegocioException("La reserva " + codigo + " no puede pasar de " + estado + " a " + nuevo + ".");
        }
    }

    public boolean puedeCancelarse() {
        return transicionPermitida(EstadoReserva.CANCELADA);
    }

    public void confirmar() throws PadreException {
        exigirTransicion(EstadoReserva.CONFIRMADA);
        if (!anticipoCubierto()) {
            throw new ReglaNegocioException("Falta el anticipo de " + Formato.dinero(calcularAnticipo())
                    + " para confirmar. Lleva pagado " + Formato.dinero(folio.totalPagos()) + ".");
        }
        estado = EstadoReserva.CONFIRMADA;
    }

    // Check-in: Confirmada, ya llego la fecha de entrada y el apartamento está Preparado
    public void registrarLlegada(LocalDateTime ahora) throws PadreException {
        exigirTransicion(EstadoReserva.EN_CURSO);
        if (ahora.toLocalDate().isBefore(entrada)) {
<<<<<<< HEAD
            throw new ReglaNegocioException("Todavia no llega la fecha de entrada (" + entrada + ").");
=======
            throw new ReglaNegocioException("Todavía no llega la fecha de entrada (" + entrada + ").");
>>>>>>> d227dd514acbe55a1dcae8e6afd91d92bac9f452
        }
        if (apartamento.getEstado() != EstadoApartamento.PREPARADO) {
            throw new ReglaNegocioException("El apartamento esta " + apartamento.getEstado() + ": debe estar PREPARADO para el check-in.");
        }
        if (!apartamento.preparacionCumplida(ahora, HORAS_PREPARACION)) {
            throw new ReglaNegocioException("Deben pasar al menos " + HORAS_PREPARACION + " horas entre la salida anterior y esta entrada.");
        }
        apartamento.setEstado(EstadoApartamento.OCUPADO, "");
        estado = EstadoReserva.EN_CURSO;
    }

<<<<<<< HEAD
    // Check-out: la reserva pasa a Finalizada y el apartamento a Pendiente de preparacion
=======
    // Check-out: la reserva pasa a Finalizada y el apartamento a Pendiente de preparación
>>>>>>> d227dd514acbe55a1dcae8e6afd91d92bac9f452
    public void registrarSalida(LocalDateTime ahora) throws PadreException {
        exigirTransicion(EstadoReserva.FINALIZADA);
        apartamento.registrarSalida(ahora);
        estado = EstadoReserva.FINALIZADA;
    }

    // Porcentaje del valor de la estancia que se retiene al cancelar
    public int porcentajeRetenido(LocalDateTime ahora) {
        Duration antelacion = Duration.between(ahora, entrada.atTime(HORA_ENTRADA));
        if (antelacion.compareTo(Duration.ofDays(politicaDias)) > 0) {
            return 0;
        }
        if (antelacion.toHours() >= politicaHoras) {
            return politicaPorcentaje;
        }
        return 100;
    }

    private Liquidacion liquidar(int porcentaje) {
        long retenido = valorEstancia * porcentaje / 100;
        long pagado = folio.totalPagos();
        // Lo cargado por servicios adicionales (desayuno, lavanderia) tampoco se devuelve
        long servicios = folio.totalCargos() - valorEstancia;
        if (servicios < 0) {
            servicios = 0;
        }
        long devolucion = pagado - retenido - servicios;
        if (devolucion < 0) {
            devolucion = 0;
        }
        return new Liquidacion(valorEstancia, pagado, porcentaje, retenido, devolucion);
    }

    // Calcula sin cambiar nada (es lo que ve el huesped antes de cancelar)
    public Liquidacion calcularLiquidacion(LocalDateTime ahora) {
        return liquidar(porcentajeRetenido(ahora));
    }

    // Deja el folio al día sin borrar nada: corrige el alojamiento, carga la penalidad y registra la devolucion
    private void aplicarLiquidacion(Liquidacion liq, LocalDateTime ahora, String motivo) throws PadreException {
        if (valorEstancia > 0) {
            folio.corregirCargo(0, motivo, ahora);
        }
        if (liq.getRetenido() > 0) {
            folio.agregarCargo("Penalidad (" + motivo + ", " + liq.getPorcentajeRetenido() + "%)", liq.getRetenido(), ahora);
        }
        if (liq.getDevolucion() > 0) {
            folio.agregarDevolucion(liq.getDevolucion(), folio.medioDelUltimoPago(), ahora);
        }
        liquidacion = liq;
    }

    public Liquidacion cancelar(LocalDateTime ahora) throws PadreException {
        exigirTransicion(EstadoReserva.CANCELADA);
        Liquidacion liquidacion = calcularLiquidacion(ahora);
        aplicarLiquidacion(liquidacion, ahora, "cancelacion");
        estado = EstadoReserva.CANCELADA;
        return liquidacion;
    }
<<<<<<< HEAD

    // Pendiente sin confirmar en 24 horas, se cancela sola y sin penalidad
    public void cancelarPorVencimiento(LocalDateTime ahora) throws PadreException {
        exigirTransicion(EstadoReserva.CANCELADA);
        aplicarLiquidacion(liquidar(0), ahora, "vencimiento de 24 horas");
        estado = EstadoReserva.CANCELADA;
    }

    // No-show, el titular no llego antes de las 10:00 p. m. del dia de entrada, se retiene el 100%
    public Liquidacion marcarNoShow(LocalDateTime ahora) throws PadreException {
        exigirTransicion(EstadoReserva.NO_SHOW);
        if (ahora.isBefore(entrada.atTime(LocalTime.of(22, 0)))) {
            throw new ReglaNegocioException("Solo se puede declarar no-show despues de las 10:00 p. m. del dia de entrada (" + entrada + ").");
        }
        Liquidacion l = liquidar(100);
        aplicarLiquidacion(l, ahora, "no-show");
        estado = EstadoReserva.NO_SHOW;
        return l;
    }
}
=======
}
>>>>>>> d227dd514acbe55a1dcae8e6afd91d92bac9f452
