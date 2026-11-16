package model;

import proyecto.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Pruebas de reportes y novedades
class ReportesTest {

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
    void reporteDeOcupacionCuentaLasNochesVendidas() throws Exception {
        Reserva reserva = aloj.crearReservaDirecta(carlos, ap101, entrada, salida, sinAcompanantes(), ahora);
        reserva.confirmar();
        String reporte = aloj.reporteOcupacion(entrada, entrada.plusDays(9));
        assertTrue(reporte.contains("AP-101 Cafetal: 3 de 10 noches (30.0%)"));
    }

    @Test
    void elReporteDeNovedadesPoneArribaAlQueTieneMas() throws Exception {
        Apartamento ap102 = aloj.getApartamentos().get(1);
        aloj.registrarNovedad(ap101, "Bombillo", ahora);
        aloj.registrarNovedad(ap102, "Grifo", ahora);
        aloj.registrarNovedad(ap102, "Toalla", ahora);
        String reporte = aloj.reporteNovedades();
        assertTrue(reporte.indexOf("AP-102") < reporte.indexOf("AP-101"));
    }

    @Test
    void registrarUnaNovedadSeCuentaEnElReporte() throws Exception {
        aloj.registrarNovedad(ap101, "Bombillo dañado", LocalDateTime.now());
        assertTrue(aloj.reporteNovedades().contains("AP-101"));
    }
}