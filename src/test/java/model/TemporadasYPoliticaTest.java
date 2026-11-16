package model;

import proyecto.model.*;
import proyecto.exception.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;



// Pruebas de temporadas y politica de cancelacion
class TemporadasYPoliticaTest {

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
    void laTemporadaSaleDeLosPeriodos() throws Exception {
        assertEquals(Temporada.BAJA, aloj.temporadaDe(entrada));
        aloj.agregarPeriodo(Temporada.ALTA, LocalDate.of(2100, 3, 1), LocalDate.of(2100, 3, 31));
        assertEquals(Temporada.ALTA, aloj.temporadaDe(entrada));
    }

    @Test
    void laPoliticaTieneQueSerValida() throws Exception {
        assertThrows(PadreException.class, () -> aloj.configurarPolitica(2, 48, 50));
        aloj.configurarPolitica(10, 72, 40);
        assertEquals(40, aloj.getPoliticaPorcentaje());
    }
}