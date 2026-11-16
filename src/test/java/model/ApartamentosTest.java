package model;

import proyecto.model.*;
import proyecto.exception.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Pruebas de apartamentos, código repetido, eliminar y estado
class ApartamentosTest {

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
    void noSeRepiteElCodigoDeApartamentoYEliminarLoOculta() throws Exception {
        assertThrows(PadreException.class, () -> aloj.crearApartamento("AP-101", "Prueba", 1, 2, "Nada", 1000, 2000, 3000));
        aloj.eliminarApartamento(ap101);
        assertEquals(2, aloj.getApartamentos().size());
    }

    @Test
    void fueraDeServicioExigeMotivo() throws Exception {
        assertThrows(PadreException.class, () -> ap101.setEstado(EstadoApartamento.FUERA_DE_SERVICIO, ""));
        ap101.setEstado(EstadoApartamento.FUERA_DE_SERVICIO, "Dano en la tuberia");
        assertEquals("Dano en la tuberia", ap101.getMotivoFueraDeServicio());
    }

    @Test
    void eliminarApartamentoConReservaActivaSeRechaza() throws Exception {
        aloj.crearReservaDirecta(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        assertThrows(PadreException.class, () -> aloj.eliminarApartamento(ap101));
        Apartamento libre = aloj.getApartamentos().get(1);
        aloj.eliminarApartamento(libre);
        assertEquals(2, aloj.getApartamentos().size());
    }
}