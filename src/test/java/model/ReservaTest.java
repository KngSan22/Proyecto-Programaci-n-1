package model;

import proyecto.model.*;
import proyecto.exception.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Pruebas de las clases de reserva
class ReservaTest {

    private Apartamento apto;
    private Huesped titular;
    private LocalDate entrada;
    private LocalDate salida;
    private LocalDateTime ahora;
    private Cotizacion cotizacion;
    private ArrayList<Ocupante> ocupantes;

    @BeforeEach
    void preparar() throws Exception {
        apto = new Apartamento("AP-101", "Cafetal", 2, 4, "Balcon y jacuzzi", 90000, 110000, 140000);
        titular = new Huesped("Carlos Cuartas", "1049258249", LocalDate.of(1990, 8, 14), "3127229553", "carlos@gmail.com", "carlos", "carlos123");
        entrada = LocalDate.of(2100, 3, 10);
        salida = entrada.plusDays(3);
        ahora = LocalDateTime.of(2100, 2, 1, 10, 0);
        // 3 noches de 90.000 = 270.000
        cotizacion = new Cotizacion(1, 1);
        cotizacion.agregarNoche("noche 1", 90000);
        cotizacion.agregarNoche("noche 2", 90000);
        cotizacion.agregarNoche("noche 3", 90000);
        ocupantes = new ArrayList<Ocupante>();
        ocupantes.add(new Ocupante("Carlos Cuartas", LocalDate.of(1990, 8, 14)));
    }

    private Reserva portal() throws Exception {
        return new ReservaPortal("R-1", apto, titular, ocupantes, entrada, salida, ahora, cotizacion, 7, 48, 50);
    }

    private Reserva directa() throws Exception {
        return new ReservaDirecta("R-2", apto, titular, ocupantes, entrada, salida, ahora, cotizacion, 7, 48, 50);
    }

    @Test
    void cadaCanalCalculaSuAnticipoYSuComision() throws Exception {
        assertEquals("PORTAL", portal().getCanal());
        assertEquals(81000, portal().calcularAnticipo());
        assertEquals(0, directa().calcularAnticipo());
        Reserva externa = new ReservaExterna("R-3", "BK-99", apto, titular, ocupantes, entrada, salida, ahora, cotizacion, 7, 48, 50);
        assertEquals(0, externa.calcularAnticipo());
        assertEquals(40500, externa.calcularComision());
        assertEquals(0, portal().calcularComision());
    }

    @Test
    void laReservaNuevaCargaElAlojamientoEnElFolio() throws Exception {
        Reserva reserva = portal();
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertEquals(270000, reserva.getFolio().saldo());
    }

    @Test
    void sinAnticipoNoSeConfirmaYConAnticipoSi() throws Exception {
        Reserva reserva = portal();
        assertThrows(PadreException.class, () -> reserva.confirmar());
        reserva.getFolio().agregarPago(81000, MedioPago.EFECTIVO, ahora);
        reserva.confirmar();
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
    }

    @Test
    void noSePuedeSaltarDeEstado() throws Exception {
        Reserva reserva = directa();
        assertThrows(PadreException.class, () -> reserva.registrarSalida(ahora));
        reserva.confirmar();
        assertThrows(PadreException.class, () -> reserva.registrarSalida(ahora));
    }

    @Test
    void laReservaPendienteVenceALas24Horas() throws Exception {
        Reserva reserva = portal();
        assertFalse(reserva.plazoVencido(ahora.plusHours(23)));
        assertTrue(reserva.plazoVencido(ahora.plusHours(24)));
    }

    @Test
    void laNocheDeSalidaNoSeOcupaNiChoca() throws Exception {
        Reserva reserva = directa();
        assertTrue(reserva.ocupaNoche(entrada));
        assertFalse(reserva.ocupaNoche(salida));
        assertFalse(reserva.chocaCon(salida, salida.plusDays(2)));
        assertTrue(reserva.chocaCon(entrada.plusDays(1), salida.plusDays(1)));
    }

    // Reserva directa confirmada y pagada completa (270.000)
    private Reserva pagadaCompleta() throws Exception {
        Reserva reserva = directa();
        reserva.confirmar();
        reserva.getFolio().agregarPago(270000, MedioPago.TARJETA, ahora);
        return reserva;
    }

    @Test
    void cancelarConMasDe7DiasDevuelveTodo() throws Exception {
        Reserva reserva = pagadaCompleta();
        Liquidacion liq = reserva.cancelar(entrada.atTime(15, 0).minusDays(10));
        assertEquals(0, liq.getPorcentajeRetenido());
        assertEquals(270000, liq.getDevolucion());
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
    }

    @Test
    void cancelarEntre2y7DiasRetieneLaMitad() throws Exception {
        Reserva reserva = pagadaCompleta();
        Liquidacion liq = reserva.cancelar(entrada.atTime(15, 0).minusDays(4));
        assertEquals(50, liq.getPorcentajeRetenido());
        assertEquals(135000, liq.getRetenido());
        assertEquals(135000, liq.getDevolucion());
    }

    @Test
    void cancelarConMenosDe48HorasRetieneTodo() throws Exception {
        Reserva reserva = pagadaCompleta();
        Liquidacion liq = reserva.cancelar(entrada.atTime(15, 0).minusDays(1));
        assertEquals(100, liq.getPorcentajeRetenido());
        assertEquals(0, liq.getDevolucion());
    }

    @Test
    void calcularLiquidacionNoCambiaLaReserva() throws Exception {
        Reserva reserva = pagadaCompleta();
        reserva.calcularLiquidacion(entrada.atTime(15, 0).minusDays(10));
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        assertTrue(reserva.puedeCancelarse());
    }

    @Test
    void noShowSoloDespuesDeLas10PM() throws Exception {
        Reserva reserva = pagadaCompleta();
        assertThrows(PadreException.class, () -> reserva.marcarNoShow(entrada.atTime(21, 0)));
        Liquidacion liq = reserva.marcarNoShow(entrada.atTime(22, 30));
        assertEquals(100, liq.getPorcentajeRetenido());
        assertEquals(EstadoReserva.NO_SHOW, reserva.getEstado());
    }

    @Test
    void unaReservaCanceladaNoSeVuelveACancelar() throws Exception {
        Reserva reserva = pagadaCompleta();
        reserva.cancelar(entrada.atTime(15, 0).minusDays(10));
        assertThrows(PadreException.class, () -> reserva.cancelar(entrada.atTime(15, 0).minusDays(9)));
        assertFalse(reserva.puedeCancelarse());
    }
}