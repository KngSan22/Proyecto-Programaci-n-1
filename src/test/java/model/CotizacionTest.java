package model;

import proyecto.exception.*;
import proyecto.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;



// Pruebas de cotización: temporadas, menores y capacidad
class CotizacionTest {

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
    void laEdadSeCalculaYLosMenoresDe6NoPagan() throws Exception {
        Ocupante nino = new Ocupante("Sofia", LocalDate.of(2020, 3, 11));
        assertEquals(5, nino.edadA(LocalDate.of(2026, 3, 10)));
        assertFalse(nino.paga(LocalDate.of(2026, 3, 10)));
        assertTrue(nino.paga(LocalDate.of(2026, 3, 11)));
    }

    @Test
    void cotizacionNochePorNocheCruzandoTemporadas() throws Exception {
        // Se define una temporada alta del 15 al 16 de diciembre del anio 2100
        aloj.agregarPeriodo(Temporada.ALTA, LocalDate.of(2100, 12, 15), LocalDate.of(2100, 12, 16));
        ArrayList<Ocupante> dos = new ArrayList<Ocupante>();
        dos.add(new Ocupante("Ana", LocalDate.of(1990, 1, 1)));
        dos.add(new Ocupante("Andres", LocalDate.of(1990, 1, 1)));
        // noche 14 baja 90.000, noches 15 y 16 alta 140.000, 2 personas
        Cotizacion cotizacion = aloj.cotizar(ap101, LocalDate.of(2100, 12, 14), LocalDate.of(2100, 12, 17), dos);
        assertEquals(740000, cotizacion.getTotal());
        assertEquals(3, cotizacion.getNoches());
    }

    @Test
    void losMenoresNoPagan() throws Exception {
        ArrayList<Ocupante> lista = new ArrayList<Ocupante>();
        lista.add(new Ocupante("Ana", LocalDate.of(1990, 1, 1)));
        lista.add(new Ocupante("Miguel", LocalDate.of(2099, 6, 1)));
        Cotizacion cotizacion = aloj.cotizar(ap101, LocalDate.of(2100, 3, 10), LocalDate.of(2100, 3, 12), lista);
        assertEquals(1, cotizacion.getPersonasQuePagan());
        assertEquals(180000, cotizacion.getTotal()); // 2 noches * 90.000 * 1 persona
    }

    @Test
    void losMenoresDe6NoPaganPeroCuentanParaLaCapacidad() throws Exception {
        ArrayList<Ocupante> gente = new ArrayList<Ocupante>();
        gente.add(new Ocupante("Adulto", LocalDate.of(1990, 1, 1)));
        gente.add(new Ocupante("Niño", entrada.minusYears(3)));
        Cotizacion cotizacion = aloj.cotizar(ap101, entrada, salida, gente);
        assertEquals(90000 * 3, cotizacion.getTotal());
        gente.add(new Ocupante("A", LocalDate.of(1990, 1, 1)));
        gente.add(new Ocupante("B", LocalDate.of(1990, 1, 1)));
        gente.add(new Ocupante("C", LocalDate.of(1990, 1, 1)));
        assertThrows(PadreException.class, () -> aloj.cotizar(ap101, entrada, salida, gente));
    }

    @Test
    void laCapacidadIncluyeALosMenores() throws Exception {
        ArrayList<Ocupante> acompanantes = new ArrayList<Ocupante>();
        acompanantes.add(new Ocupante("A", LocalDate.of(1990, 1, 1)));
        acompanantes.add(new Ocupante("B", LocalDate.of(1990, 1, 1)));
        acompanantes.add(new Ocupante("Bebe", LocalDate.now().minusYears(1)));
        acompanantes.add(new Ocupante("Bebe2", LocalDate.now().minusYears(1)));
        // titular + 4 = 5 personas y el apartamento admite 4
        assertThrows(PadreException.class, () -> aloj.crearReservaDirecta(carlos, ap101, entrada, salida, acompanantes, ahora));
    }

    @Test
    void elValorQuedaCongeladoSiCambianLasTarifas() throws Exception {
        Reserva reserva = aloj.crearReservaPortal(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        long antes = reserva.getValorEstancia();
        ap101.definirTarifas(500000, 500000, 500000);
        assertEquals(antes, reserva.getValorEstancia());
    }
}