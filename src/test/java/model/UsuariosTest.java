package model;

import org.junit.jupiter.api.*;
import proyecto.model.*;
import proyecto.exception.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// Pruebas de usuarios
class UsuariosTest {

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
    void inicioDeSesionYRoles() throws Exception {
        assertEquals("ADMINISTRADOR", aloj.iniciarSesion("admin", "admin123").getRol());
        assertEquals("RECEPCIONISTA", aloj.iniciarSesion("recepcion", "recep123").getRol());
        assertEquals("HUESPED", aloj.iniciarSesion("carlos", "carlos123").getRol());
        assertThrows(PadreException.class, () -> aloj.iniciarSesion("admin", "mala"));
    }

    @Test
    void recepcionistaDesvinculadoNoEntra() throws Exception {
        aloj.desvincularRecepcionista(aloj.getRecepcionistas().get(0));
        assertThrows(PadreException.class, () -> aloj.iniciarSesion("recepcion", "recep123"));
    }

    @Test
    void noSePuedeRepetirUsuarioNiDocumento() throws Exception {
        assertThrows(PadreException.class, () -> aloj.registrarHuesped("Otro", "99999", LocalDate.of(2000, 1, 1), "3050545555", "otro@gmail.com", "carlos", "clave123"));
        assertThrows(PadreException.class, () -> aloj.registrarHuesped("Otro", "1049258249", LocalDate.of(2000, 1, 1), "3050545555", "otro@gmail.com", "otro", "clave123"));
    }

    @Test
    void elHuespedPuedeEditarSuPerfil() throws Exception {
        Huesped carlos = aloj.buscarHuesped("1049258249");
        aloj.editarPerfil(carlos, "Carlos Perez", "3214567890", "carlos@hotmail.com");
        assertEquals("Carlos Perez", carlos.getNombre());
        assertThrows(PadreException.class, () -> aloj.editarPerfil(carlos, "Carlos", "3214567890", "elcorreodecarlos.com"));
    }

    @Test
    void laClaveCorrectaSeValida() throws Exception {
        assertTrue(carlos.claveCorrecta("carlos123"));
        assertFalse(carlos.claveCorrecta("otra"));
    }
}