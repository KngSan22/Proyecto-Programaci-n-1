package proyecto.model;

import proyecto.exception.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Los controladores solo llaman a estos metodos.
public class Alojamiento {

    private ArrayList<Apartamento> apartamentos = new ArrayList<Apartamento>();
    private ArrayList<Huesped> huespedes = new ArrayList<Huesped>();
    private ArrayList<Recepcionista> recepcionistas = new ArrayList<Recepcionista>();
    private ArrayList<Administrador> administradores = new ArrayList<Administrador>();
    private ArrayList<PeriodoTemporada> periodos = new ArrayList<PeriodoTemporada>();
    private ArrayList<Bloqueo> bloqueos = new ArrayList<Bloqueo>();
    private ArrayList<Novedad> novedades = new ArrayList<Novedad>();

    // Politica de cancelacion, configurada por el admin
    private int politicaDias = 7;
    private int politicaHoras = 48;
    private int politicaPorcentaje = 50;

    // Datos de ejemplo
    public void cargarDatosDeEjemplo() throws PadreException {
        administradores.add(new Administrador("Administrador General", "1049359485", "admin", "admin123"));
        recepcionistas.add(new Recepcionista("Laura Cardona", "1058230540", "3115104920", "laura@correo.com", "recepcion", "recep123"));
        huespedes.add(new Huesped("Carlos Cuartas", "1049258249", LocalDate.of(1990, 8, 14), "3127229553", "carlos@gmail.com", "carlos", "carlos123"));
        apartamentos.add(new Apartamento("AP-101", "Cafetal", 2, 4, "Balcon y jacuzzi", 90000, 110000, 140000));
        apartamentos.add(new Apartamento("AP-102", "Palma de Cera", 1, 2, "Balcon, jacuzzi y chimenea", 110000, 130000, 170000));
        apartamentos.add(new Apartamento("AP-201", "Martini", 3, 6, "Balcon y chimenea", 80000, 100000, 130000));
        int year = LocalDate.now().getYear();
        periodos.add(new PeriodoTemporada(Temporada.ALTA, LocalDate.of(year, 12, 15), LocalDate.of(year + 1, 1, 15)));
        periodos.add(new PeriodoTemporada(Temporada.ALTA, LocalDate.of(year, 6, 15), LocalDate.of(year, 7, 15)));
        periodos.add(new PeriodoTemporada(Temporada.MEDIA, LocalDate.of(year, 10, 10), LocalDate.of(year, 10, 13)));
    }

    // Usuarios

    // Busca el usuario en las tres listas, si no coincide lanza un exception
    public Usuario iniciarSesion(String usuario, String clave) throws PadreException {
        for (Administrador administrador : administradores) {
            if (administrador.getUsuario().equals(usuario) && administrador.claveCorrecta(clave)) {
                return administrador;
            }
        }
        for (Recepcionista recepcionista : recepcionistas) {
            if (recepcionista.getUsuario().equals(usuario) && recepcionista.claveCorrecta(clave)) {
                if (!recepcionista.isActivo()) {
                    throw new ReglaNegocioException("Este recepcionista fue desvinculado y no puede ingresar.");
                }
                return recepcionista;
            }
        }
        for (Huesped huesped : huespedes) {
            if (huesped.getUsuario().equals(usuario) && huesped.claveCorrecta(clave)) {
                return huesped;
            }
        }
        throw new ReglaNegocioException("Usuario o contrasena incorrectos.");
    }

    private boolean usuarioOcupado(String usuario, String documento) {
        for (Administrador administrador : administradores) {
            if (administrador.getUsuario().equals(usuario) || administrador.getDocumento().equals(documento)) {
                return true;
            }
        }
        for (Recepcionista recepcionista : recepcionistas) {
            if (recepcionista.getUsuario().equals(usuario) || recepcionista.getDocumento().equals(documento)) {
                return true;
            }
        }
        for (Huesped huesped : huespedes) {
            if (huesped.getUsuario().equals(usuario) || huesped.getDocumento().equals(documento)) {
                return true;
            }
        }
        return false;
    }

    public Huesped registrarHuesped(String nombre, String documento, LocalDate nacimiento, String telefono, String correo, String usuario, String clave) throws PadreException {
        Huesped nuevo = new Huesped(nombre, documento, nacimiento, telefono, correo, usuario, clave);
        if (usuarioOcupado(nuevo.getUsuario(), nuevo.getDocumento())) {
            throw new DuplicadoException("Ya existe alguien con ese usuario o ese documento.");
        }
        huespedes.add(nuevo);
        return nuevo;
    }

    public Huesped buscarHuesped(String documento) throws PadreException {
        for (Huesped huesped : huespedes) {
            if (huesped.getDocumento().equals(documento.trim())) {
                return huesped;
            }
        }
        throw new NoEncontradoException("No existe un huésped con documento " + documento + ".");
    }

    public Recepcionista contratarRecepcionista(String nombre, String documento, String telefono, String correo, String usuario, String clave) throws PadreException {
        Recepcionista nuevo = new Recepcionista(nombre, documento, telefono, correo, usuario, clave);
        if (usuarioOcupado(nuevo.getUsuario(), nuevo.getDocumento())) {
            throw new DuplicadoException("Ya existe alguien con ese usuario o ese documento.");
        }
        recepcionistas.add(nuevo);
        return nuevo;
    }

    public ArrayList<Recepcionista> getRecepcionistas() {
        return new ArrayList<Recepcionista>(recepcionistas);
    }

    // Apartamentos

    public Apartamento crearApartamento(String codigo, String nombre, int dormitorios, int capacidad, String caracteristicas, long baja, long media, long alta) throws PadreException {
        for (Apartamento apartamento : apartamentos) {
            if (apartamento.getCodigo().equalsIgnoreCase(codigo.trim())) {
                throw new DuplicadoException("Ya existe un apartamento con el codigo " + codigo + ".");
            }
        }
        Apartamento nuevo = new Apartamento(codigo, nombre, dormitorios, capacidad, caracteristicas, baja, media, alta);
        apartamentos.add(nuevo);
        return nuevo;
    }

    // Solo los activos (los eliminados no se muestran)
    public ArrayList<Apartamento> getApartamentos() {
        ArrayList<Apartamento> lista = new ArrayList<Apartamento>();
        for (Apartamento apartamento : apartamentos) {
            if (apartamento.isActivo()) {
                lista.add(apartamento);
            }
        }
        return lista;
    }

    public void eliminarApartamento(Apartamento apto) throws PadreException {
        // Pendiente (reservas): no dejar eliminar si tiene una reserva activa
        apto.eliminar();
    }

    // Ocupado solo lo pone el check-in y solo lo quita el check-out
    public void cambiarEstadoApartamento(Apartamento apto, EstadoApartamento nuevo, String motivo) throws PadreException {
        if (nuevo == EstadoApartamento.OCUPADO) {
            throw new ReglaNegocioException("El estado OCUPADO solo se asigna con el check-in de una reserva.");
        }
        if (apto.getEstado() == EstadoApartamento.OCUPADO) {
            throw new ReglaNegocioException("El apartamento esta ocupado: se libera con el check-out.");
        }
        apto.setEstado(nuevo, motivo);
    }

    public void registrarNovedad(Apartamento apto, String descripcion, LocalDateTime ahora) throws PadreException {
        novedades.add(new Novedad(apto, descripcion, ahora));
    }

    public ArrayList<Novedad> getNovedades() {
        return new ArrayList<Novedad>(novedades);
    }

    // Temporadas, politica y bloqueos

    public void agregarPeriodo(Temporada temporada, LocalDate desde, LocalDate hasta) throws PadreException {
        periodos.add(new PeriodoTemporada(temporada, desde, hasta));
    }

    public void eliminarPeriodo(PeriodoTemporada periodo) {
        periodos.remove(periodo);
    }

    public ArrayList<PeriodoTemporada> getPeriodos() {
        return new ArrayList<PeriodoTemporada>(periodos);
    }

    // Si la fecha no está en ningún periodo es BAJA, Si está en varios, gana la más cara.
    public Temporada temporadaDe(LocalDate fecha) {
        Temporada resultado = Temporada.BAJA;
        for (PeriodoTemporada p : periodos) {
            if (p.contiene(fecha)) {
                if (p.getTemporada() == Temporada.ALTA) {
                    resultado = Temporada.ALTA;
                } else if (resultado == Temporada.BAJA) {
                    resultado = Temporada.MEDIA;
                }
            }
        }
        return resultado;
    }

    public void configurarPolitica(int dias, int horas, int porcentaje) throws PadreException {
        if (dias < 1 || horas < 1 || horas >= dias * 24 || porcentaje < 1 || porcentaje > 99) {
            throw new DatoInvalidoException("Politica no valida: las horas deben ser menores que los dias*24 y el porcentaje entre 1 y 99.");
        }
        politicaDias = dias;
        politicaHoras = horas;
        politicaPorcentaje = porcentaje;
    }

    public int getPoliticaDias() {
        return politicaDias;
    }

    public int getPoliticaHoras() {
        return politicaHoras;
    }

    public int getPoliticaPorcentaje() {
        return politicaPorcentaje;
    }

    public void registrarBloqueo(Apartamento apto, LocalDate desde, LocalDate hasta, String motivo) throws PadreException {
        Bloqueo nuevo = new Bloqueo(apto, desde, hasta, motivo);
        // Pendiente (reservas): revisar que no choque con una reserva activa
        bloqueos.add(nuevo);
    }

    public void eliminarBloqueo(Bloqueo b) {
        bloqueos.remove(b);
    }

    public ArrayList<Bloqueo> getBloqueos() {
        return new ArrayList<Bloqueo>(bloqueos);
    }

    // Disponibilidad y cotizacion

    // Devuelve null si esta libre, si no, el motivo por el que no se puede
    private String motivoNoDisponible(Apartamento apto, LocalDate entrada, LocalDate salida) {
        for (Bloqueo bloqueo : bloqueos) {
            if (bloqueo.getApartamento() == apto && bloqueo.chocaCon(entrada, salida)) {
                return "El apartamento " + apto.getCodigo() + " esta bloqueado (" + bloqueo.getMotivo() + ") en esas fechas.";
            }
        }
        // Pendiente (reservas): tambien revisar las reservas activas del apartamento
        return null;
    }

    private void validarFechas(LocalDate entrada, LocalDate salida) throws PadreException {
        if (!salida.isAfter(entrada)) {
            throw new ReglaNegocioException("La fecha de salida debe ser posterior a la de entrada (minimo una noche).");
        }
    }

    public ArrayList<Apartamento> buscarDisponibles(LocalDate entrada, LocalDate salida, int personas) throws PadreException {
        validarFechas(entrada, salida);
        ArrayList<Apartamento> libres = new ArrayList<Apartamento>();
        for (Apartamento apartamento : getApartamentos()) {
            if (apartamento.getCapacidad() >= personas && motivoNoDisponible(apartamento, entrada, salida) == null) {
                libres.add(apartamento);
            }
        }
        return libres;
    }

    // El valor es la suma, noche por noche, de la tarifa de la temporada de esa noche
    // multiplicada por las personas de 6 años o más (edad a la fecha de entrada)
    public Cotizacion cotizar(Apartamento apto, LocalDate entrada, LocalDate salida, ArrayList<Ocupante> ocupantes) throws PadreException {
        validarFechas(entrada, salida);
        if (ocupantes.size() == 0) {
            throw new DatoInvalidoException("Debe haber al menos un ocupante.");
        }
        // Todos cuentan para la capacidad, paguen o no
        if (ocupantes.size() > apto.getCapacidad()) {
            throw new ReglaNegocioException("El apartamento admite máximo " + apto.getCapacidad() + " personas y se indicaron " + ocupantes.size() + ".");
        }
        int pagan = 0;
        for (Ocupante o : ocupantes) {
            if (o.paga(entrada)) {
                pagan = pagan + 1;
            }
        }
        Cotizacion cotizada = new Cotizacion(pagan, ocupantes.size());
        LocalDate noche = entrada;
        while (noche.isBefore(salida)) {
            Temporada temporada = temporadaDe(noche);
            long tarifa = apto.tarifaDe(temporada);
            cotizada.agregarNoche(noche + " | " + temporada + " | " + Formato.dinero(tarifa) + " x " + pagan + " = " + Formato.dinero(tarifa * pagan), tarifa * pagan);
            noche = noche.plusDays(1);
        }
        return cotizada;
    }

    // Perfiles y empleados

    public void editarPerfil(Huesped huesped, String nombre, String telefono, String correo) throws PadreException {
        huesped.editarDatos(nombre, telefono, correo);
    }

    public void editarRecepcionista(Recepcionista r, String nombre, String telefono, String correo) throws PadreException {
        r.editarDatos(nombre, telefono, correo);
    }

    public void desvincularRecepcionista(Recepcionista r) throws PadreException {
        if (!r.isActivo()) {
            throw new ReglaNegocioException("El recepcionista ya estaba desvinculado.");
        }
        r.desvincular();
    }

    public void modificarApartamento(Apartamento apto, String nombre, int dormitorios, int capacidad, String caracteristicas, long baja, long media, long alta) throws PadreException {
        // Pendiente (reservas): no bajar la capacidad por debajo de los ocupantes de una reserva activa
        apto.modificar(nombre, dormitorios, capacidad, caracteristicas);
        apto.definirTarifas(baja, media, alta);
    }

    // Reportes

    public String reporteNovedades() {
        String texto = "NOVEDADES POR APARTAMENTO\n";
        for (Apartamento apartamento : apartamentos) {
            int cantidad = 0;
            for (Novedad n : novedades) {
                if (n.getApartamento() == apartamento) {
                    cantidad = cantidad + 1;
                }
            }
            if (cantidad > 0) {
                texto += apartamento.getCodigo() + " " + apartamento.getNombre() + ": " + cantidad + " novedades\n";
            }
        }
        return texto;
    }
}
