package model;

import proyecto.exception.*;
import proyecto.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Pruebas de disponibilidad, fechas, doble venta y bloqueos
class DisponibilidadTest {

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
        // Fechas lejanas (anio 2100) para que siempre sean temporada baja y esten en el futuro
        entrada = LocalDate.of(2100, 3, 10);
        salida = entrada.plusDays(3);
        ahora = LocalDateTime.now();
    }

    private ArrayList<Ocupante> sinAcompanantes() {
        return new ArrayList<Ocupante>();
    }

    @Test
    void noHayDobleVentaEntreCanales() throws Exception {
        aloj.crearReservaPortal(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        assertThrows(PadreException.class, () -> aloj.crearReservaDirecta(carlos, ap101, entrada.plusDays(2), salida.plusDays(2), sinAcompanantes(), ahora));
        assertEquals(1, aloj.getReservas().size());
    }

    @Test
    void elDiaDeSalidaPuedeSerElDeEntradaDeOtra() throws Exception {
        aloj.crearReservaPortal(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        Reserva otra = aloj.crearReservaDirecta(carlos, ap101, salida, salida.plusDays(1), sinAcompanantes(), ahora);
        assertNotNull(otra);
    }

    @Test
    void noSeReservaEnElPasadoNiConSalidaAnteriorALaEntrada() throws Exception {
        assertThrows(PadreException.class, () -> aloj.crearReservaPortal(carlos, ap101, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), sinAcompanantes(), ahora));
        assertThrows(PadreException.class, () -> aloj.crearReservaPortal(carlos, ap101, entrada, entrada, sinAcompanantes(), ahora));
    }

    @Test
    void lasFechasDebenTenerMinimoUnaNoche() throws Exception {
        assertThrows(PadreException.class, () -> aloj.buscarDisponibles(entrada, entrada, 2));
    }

    @Test
    void unBloqueoImpideReservar() throws Exception {
        aloj.registrarBloqueo(ap101, entrada.plusDays(1), entrada.plusDays(1), "Mantenimiento electrico");
        assertThrows(PadreException.class, () -> aloj.crearReservaPortal(carlos, ap101, entrada, salida, sinAcompanantes(), ahora));
        assertFalse(aloj.buscarDisponibles(entrada, salida, 2).contains(ap101));
    }

    @Test
    void unBloqueoQuitaElApartamentoDeLosDisponibles() throws Exception {
        assertTrue(aloj.buscarDisponibles(entrada, salida, 2).contains(ap101));
        aloj.registrarBloqueo(ap101, entrada.plusDays(1), entrada.plusDays(1), "Mantenimiento");
        assertFalse(aloj.buscarDisponibles(entrada, salida, 2).contains(ap101));
        assertThrows(PadreException.class, () -> aloj.registrarBloqueo(ap101, entrada, entrada, ""));
    }
}
