package model;

import proyecto.exception.*;
import proyecto.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Pruebas del ciclo de vida de la reserva
class ReservaCicloTest {

    private Alojamiento aloj;
    private Huesped carlos;
    private Apartamento ap101; // capacidad 4, baja 90.000
    private LocalDate entrada;
    private LocalDate salida;
    private LocalDateTime ahora;

    @BeforeEach
    void preparar() throws Exception {
        aloj = new Alojamiento();
        aloj.cargarDatosDeEjemplo();
        carlos = aloj.buscarHuesped("1049258249");
        ap101 = aloj.getApartamentos().get(0);
        entrada = LocalDate.of(2100, 3, 10);
        salida = entrada.plusDays(3);
        ahora = LocalDateTime.now();
    }

    private ArrayList<Ocupante> sinAcompanantes() {
        return new ArrayList<Ocupante>();
    }

    @Test
    void elPortalSeConfirmaConElAnticipoDel30() throws Exception {
        Reserva reserva = aloj.crearReservaPortal(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        long valor = reserva.getValorEstancia();
        assertThrows(PadreException.class, () -> reserva.confirmar());
        aloj.registrarPago(reserva, valor * 30 / 100, MedioPago.TARJETA, ahora);
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
    }

    @Test
    void unaReservaPendienteVenceALas24Horas() throws Exception {
        Reserva reserva = aloj.crearReservaPortal(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        assertEquals(0, aloj.liberarVencidas(ahora.plusHours(23)));
        assertEquals(1, aloj.liberarVencidas(ahora.plusHours(24)));
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertTrue(aloj.buscarDisponibles(entrada, salida, 2).contains(ap101));
    }

    @Test
    void cicloDeVidaYCheckInCheckOut() throws Exception {
        Reserva reserva = aloj.crearReservaDirecta(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        // Pendiente no puede pasar directo a En curso
        assertThrows(PadreException.class, () -> reserva.registrarLlegada(entrada.atTime(15, 0)));
        reserva.confirmar();
        // Antes de la fecha de entrada no hay check-in
        assertThrows(PadreException.class, () -> reserva.registrarLlegada(entrada.minusDays(1).atTime(20, 0)));
        // Con el apartamento sin preparar tampoco
        ap101.setEstado(EstadoApartamento.PENDIENTE_PREPARACION, "");
        assertThrows(PadreException.class, () -> reserva.registrarLlegada(entrada.atTime(15, 0)));
        ap101.setEstado(EstadoApartamento.PREPARADO, "");
        reserva.registrarLlegada(entrada.atTime(15, 0));
        assertEquals(EstadoApartamento.OCUPADO, ap101.getEstado());
        reserva.registrarSalida(salida.atTime(11, 0));
        assertEquals(EstadoReserva.FINALIZADA, reserva.getEstado());
        assertEquals(EstadoApartamento.PENDIENTE_PREPARACION, ap101.getEstado());
        // Finalizada es estado final
        assertThrows(PadreException.class, () -> reserva.cancelar(ahora));
    }

    @Test
    void noShowSoloDespuesDeLas10PM() throws Exception {
        Reserva reserva = aloj.crearReservaDirecta(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        reserva.confirmar();
        assertThrows(PadreException.class, () -> reserva.marcarNoShow(entrada.atTime(21, 59)));
        reserva.marcarNoShow(entrada.atTime(22, 0));
        assertEquals(EstadoReserva.NO_SHOW, reserva.getEstado());
        assertFalse(reserva.estaActiva());
    }

    @Test
    void entreUnaSalidaYLaSiguienteEntradaPasan3HorasDePreparacion() throws Exception {
        Reserva primera = aloj.crearReservaDirecta(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        Reserva segunda = aloj.crearReservaDirecta(carlos, ap101, salida, salida.plusDays(1), sinAcompanantes(), ahora);
        primera.confirmar();
        segunda.confirmar();
        primera.registrarLlegada(entrada.atTime(15, 0));
        primera.registrarSalida(salida.atTime(11, 0));
        ap101.setEstado(EstadoApartamento.PREPARADO, "");
        // Solo pasaron 2 horas desde la salida
        assertThrows(PadreException.class, () -> segunda.registrarLlegada(salida.atTime(13, 0)));
        segunda.registrarLlegada(salida.atTime(15, 0));
        assertEquals(EstadoReserva.EN_CURSO, segunda.getEstado());
    }

    @Test
    void elFolioNoSeEditaSeCorrigeConUnMovimientoContrario() throws Exception {
        Reserva reserva = aloj.crearReservaDirecta(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        long valor = reserva.getValorEstancia();
        aloj.registrarPago(reserva, 100000, MedioPago.EFECTIVO, ahora);
        reserva.getFolio().corregirPago(0, "se registro mal", ahora);
        assertEquals(valor, reserva.getFolio().saldo());
        assertEquals(2, reserva.getFolio().getPagos().size()); // el original sigue ahi
        assertThrows(PadreException.class, () -> reserva.getFolio().corregirPago(0, "otra vez", ahora));
    }

    @Test
    void elFolioCalculaElSaldoYNoAceptaValoresEnCero() throws Exception {
        Folio folio = new Folio();
        LocalDateTime ahora = LocalDateTime.now();
        folio.agregarCargo("Alojamiento", 100000, ahora);
        folio.agregarPago(40000, MedioPago.EFECTIVO, ahora);
        assertEquals(60000, folio.saldo());
        assertThrows(PadreException.class, () -> folio.agregarPago(0, MedioPago.TARJETA, ahora));
        assertThrows(PadreException.class, () -> folio.agregarCargo("Servicio", 0, ahora));
    }
}