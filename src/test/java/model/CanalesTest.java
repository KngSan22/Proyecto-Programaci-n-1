package model;

import proyecto.model.*;
import proyecto.exception.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Pruebas de canales
class CanalesTest {

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
    void reservaExternaConChoqueSeRechazaYSeRegistraElConflicto() throws Exception {
        Reserva existente = aloj.crearReservaPortal(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        assertThrows(PadreException.class, () -> aloj.crearReservaExterna("BKG-1", carlos, ap101, entrada, salida, sinAcompanantes(), ahora));
        assertEquals(1, aloj.getConflictos().size());
        assertEquals(1, aloj.getReservas().size());
        assertEquals(EstadoReserva.PENDIENTE, existente.getEstado());
    }

    @Test
    void unCodigoExternoNoSeRepiteYCobraComisionDel15() throws Exception {
        Apartamento ap201 = aloj.getApartamentos().get(2);
        Reserva reserva = aloj.crearReservaExterna("BKG-2", carlos, ap201, entrada, salida, sinAcompanantes(), ahora);
        assertEquals(reserva.getValorEstancia() * 15 / 100, reserva.calcularComision());
        assertThrows(PadreException.class, () -> aloj.crearReservaExterna("BKG-2", carlos, ap101, entrada, salida, sinAcompanantes(), ahora));
    }

    @Test
    void soloElChoqueDeNochesEsConflictoDeCanal() throws Exception {
        // Seis personas en un apartamento de capacidad 4: se rechaza, pero no es un conflicto de canal
        ArrayList<Ocupante> muchos = new ArrayList<Ocupante>();
        for (int i = 0; i < 5; i++) {
            muchos.add(new Ocupante("Persona" + i, LocalDate.of(1990, 1, 1)));
        }
        assertThrows(PadreException.class, () -> aloj.crearReservaExterna("BKG-9", carlos, ap101, entrada, salida, muchos, ahora));
        assertEquals(0, aloj.getConflictos().size());
    }
}