package model;

import proyecto.model.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Pruebas de cancelación según la política y servicios
class CancelacionTest {

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
        // Fechas lejanas para que siempre sean temporada baja y estén en el futuro
        entrada = LocalDate.of(2100, 3, 10);
        salida = entrada.plusDays(3);
        ahora = LocalDateTime.now();
    }

    private ArrayList<Ocupante> sinAcompanantes() {
        return new ArrayList<Ocupante>();
    }

    @Test
    void cancelarMasDe7DiasDevuelveTodo() throws Exception {
        Reserva reserva = aloj.crearReservaDirecta(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        long valor = reserva.getValorEstancia();
        aloj.registrarPago(reserva, valor, MedioPago.EFECTIVO, ahora);
        Liquidacion liq = aloj.cancelarReserva(reserva, ahora);
        assertEquals(valor, liq.getDevolucion());
        assertEquals(0, reserva.getFolio().saldo());
    }

    @Test
    void cancelarEntre2y7DiasRetieneElMitad() throws Exception {
        Reserva reserva = aloj.crearReservaDirecta(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        long valor = reserva.getValorEstancia();
        aloj.registrarPago(reserva, valor, MedioPago.EFECTIVO, ahora);
        LocalDateTime cuatroDiasAntes = entrada.minusDays(4).atTime(10, 0);
        Liquidacion vista = reserva.calcularLiquidacion(cuatroDiasAntes);
        assertEquals(50, vista.getPorcentajeRetenido());
        assertEquals(valor - valor / 2, vista.getDevolucion());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado()); // calcular no cambia nada
    }

    @Test
    void cancelarConMenosDe48HorasRetieneTodo() throws Exception {
        Reserva reserva = aloj.crearReservaDirecta(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        aloj.registrarPago(reserva, reserva.getValorEstancia(), MedioPago.EFECTIVO, ahora);
        Liquidacion liq = reserva.cancelar(entrada.minusDays(1).atTime(20, 0));
        assertEquals(100, liq.getPorcentajeRetenido());
        assertEquals(0, liq.getDevolucion());
    }

    @Test
    void losServiciosAdicionalesNoSeDevuelvenAlCancelar() throws Exception {
        Reserva reserva = aloj.crearReservaDirecta(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        long valor = reserva.getValorEstancia();
        aloj.registrarServicio(reserva, "Desayuno", 20000, ahora);
        aloj.registrarPago(reserva, valor + 20000, MedioPago.EFECTIVO, ahora);
        Liquidacion liq = aloj.cancelarReserva(reserva, ahora);
        assertEquals(valor, liq.getDevolucion());
        assertEquals(0, reserva.getFolio().saldo());
    }
}
