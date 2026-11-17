package proyecto.model;

import proyecto.exception.*;

import java.time.*;
import java.util.ArrayList;

// Los controladores solo llaman a estos métodos.
public class Alojamiento {

    private ArrayList<Apartamento> apartamentos = new ArrayList<Apartamento>();
    private ArrayList<Huesped> huespedes = new ArrayList<Huesped>();
    private ArrayList<Recepcionista> recepcionistas = new ArrayList<Recepcionista>();
    private ArrayList<Administrador> administradores = new ArrayList<Administrador>();
    private ArrayList<PeriodoTemporada> periodos = new ArrayList<PeriodoTemporada>();
    private ArrayList<Bloqueo> bloqueos = new ArrayList<Bloqueo>();
    private ArrayList<Reserva> reservas = new ArrayList<Reserva>();
    private ArrayList<Novedad> novedades = new ArrayList<Novedad>();
    private ArrayList<String> conflictos = new ArrayList<String>();

    // Política de cancelación, configurada por el admin
    private int politicaDias = 7;
    private int politicaHoras = 48;
    private int politicaPorcentaje = 50;

    private int contadorReservas = 0;

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

    private void validarNumerico(String valor, String campo, int min, int max) throws PadreException {
        if (valor == null || !valor.trim().matches("\\d{" + min + "," + max + "}")) {
            throw new DatoInvalidoException("El " + campo + " debe tener solo números (entre " + min + " y " + max + " dígitos).");
        }
    }

    private void validarNacimiento(LocalDate nacimiento) throws PadreException {
        if (nacimiento == null) {
            throw new DatoInvalidoException("La fecha de nacimiento es obligatoria.");
        }
        if (nacimiento.isAfter(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha de nacimiento no puede ser posterior a hoy.");
        }
    }

    public Huesped registrarHuesped(String nombre, String documento, LocalDate nacimiento, String telefono, String correo, String usuario, String clave) throws PadreException {
        validarNumerico(documento, "documento", 5, 12);
        validarNumerico(telefono, "teléfono", 7, 10);
        validarNacimiento(nacimiento);
        // El titular es el responsable de la reserva y del pago: debe ser mayor de edad
        if (Period.between(nacimiento, LocalDate.now()).getYears() < 18) {
            throw new ReglaNegocioException("El huésped debe ser mayor de edad (18 años) para registrarse y reservar.");
        }
        Huesped nuevo = new Huesped(nombre, documento, nacimiento, telefono.trim(), correo, usuario, clave);
        if (usuarioOcupado(nuevo.getUsuario(), nuevo.getDocumento())) {
            throw new DuplicadoException("Ya existe alguien con ese usuario o ese documento.");
        }
        huespedes.add(nuevo);
        return nuevo;
    }

    public ArrayList<Huesped> getHuespedes() {
        return new ArrayList<Huesped>(huespedes);
    }

    public Huesped buscarHuesped(String documento) throws PadreException {
        for (Huesped huesped : huespedes) {
            if (huesped.getDocumento().equals(documento.trim())) {
                return huesped;
            }
        }
        throw new NoEncontradoException("No existe un huésped con documento " + documento + ".");
    }

    // Validaciones de los datos de contacto del recepcionista
    private void validarDatosRecepcionista(String documento, String telefono, String correo) throws PadreException {
        if (documento != null && !documento.trim().matches("\\d{5,10}")) {
            throw new DatoInvalidoException("El documento debe tener solo números, entre 5 y 10 dígitos.");
        }
        if (telefono == null || !telefono.trim().matches("\\d{7,10}")) {
            throw new DatoInvalidoException("El teléfono debe tener solo números, entre 7 y 10 dígitos.");
        }
        if (correo == null || correo.trim().indexOf('@') < 1 || correo.trim().indexOf('@') == correo.trim().length() - 1) {
            throw new DatoInvalidoException("El correo no es válido: debe contener '@' con texto antes y después.");
        }
    }

    public Recepcionista contratarRecepcionista(String nombre, String documento, String telefono, String correo, String usuario, String clave) throws PadreException {
        validarDatosRecepcionista(documento, telefono, correo);
        Recepcionista nuevo = new Recepcionista(nombre, documento, telefono.trim(), correo.trim(), usuario, clave);
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
        for (Reserva reserva : reservas) {
            if (reserva.getApartamento() == apto && reserva.estaActiva()) {
                throw new ReglaNegocioException("El apartamento tiene la reserva activa " + reserva.getCodigo() + " y no se puede eliminar.");
            }
        }
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
        if (desde == null || hasta == null) {
            throw new DatoInvalidoException("Debe indicar la fecha inicial y la final.");
        }
        if (desde.isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("La temporada debe empezar hoy o en una fecha futura.");
        }
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
        if (desde == null || desde.isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("El bloqueo debe empezar hoy o en una fecha futura.");
        }
        for (Reserva reserva : reservas) {
            if (reserva.getApartamento() == apto && reserva.estaActiva() && nuevo.chocaCon(reserva.getEntrada(), reserva.getSalida())) {
                throw new ReglaNegocioException("El bloqueo choca con la reserva activa " + reserva.getCodigo() + ".");
            }
        }
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
        for (Reserva reserva : reservas) {
            if (reserva.getApartamento() == apto && reserva.estaActiva() && reserva.chocaCon(entrada, salida)) {
                return "El apartamento " + apto.getCodigo() + " ya tiene la reserva " + reserva.getCodigo() + " en esas fechas.";
            }
        }
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
            if (apartamento.getEstado() != EstadoApartamento.FUERA_DE_SERVICIO && apartamento.getCapacidad() >= personas && motivoNoDisponible(apartamento, entrada, salida) == null) {
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

    // Crear reservas

    // Hace las validaciones comunes y devuelve la cotización lista
    private Cotizacion validarReserva(Huesped titular, Apartamento apto, LocalDate entrada, LocalDate salida, ArrayList<Ocupante> acompanantes, ArrayList<Ocupante> todos) throws PadreException {
        validarFechas(entrada, salida);
        if (entrada.isBefore(LocalDate.now())) {
            throw new ReglaNegocioException("No se pueden crear reservas con fecha de entrada anterior a hoy.");
        }
        // Un apartamento fuera de servicio no se vende; para cerrar fechas concretas se usan los bloqueos
        if (apto.getEstado() == EstadoApartamento.FUERA_DE_SERVICIO) {
            throw new ReglaNegocioException("El apartamento " + apto.getCodigo() + " esta fuera de servicio ("
                    + apto.getMotivoFueraDeServicio() + ") y no se puede reservar.");
        }
        // El titular también se aloja, es el primer ocupante
        todos.add(new Ocupante(titular.getNombre(), titular.getFechaNacimiento()));
        todos.addAll(acompanantes);
        Cotizacion cot = cotizar(apto, entrada, salida, todos);
        String motivo = motivoNoDisponible(apto, entrada, salida);
        if (motivo != null) {
            throw new PadreException(motivo);
        }
        return cot;
    }

    private String nuevoCodigo() {
        contadorReservas++;
        return "R-" + contadorReservas;
    }

    public Reserva crearReservaPortal(Huesped titular, Apartamento apto, LocalDate entrada, LocalDate salida, ArrayList<Ocupante> acompanantes, LocalDateTime ahora) throws PadreException {
        ArrayList<Ocupante> todos = new ArrayList<Ocupante>();
        Cotizacion cot = validarReserva(titular, apto, entrada, salida, acompanantes, todos);
        Reserva r = new ReservaPortal(nuevoCodigo(), apto, titular, todos, entrada, salida, ahora, cot, politicaDias, politicaHoras, politicaPorcentaje);
        reservas.add(r);
        return r;
    }

    public Reserva crearReservaDirecta(Huesped titular, Apartamento apto, LocalDate entrada, LocalDate salida, ArrayList<Ocupante> acompanantes, LocalDateTime ahora) throws PadreException {
        ArrayList<Ocupante> todos = new ArrayList<Ocupante>();
        Cotizacion cot = validarReserva(titular, apto, entrada, salida, acompanantes, todos);
        Reserva r = new ReservaDirecta(nuevoCodigo(), apto, titular, todos, entrada, salida, ahora, cot, politicaDias, politicaHoras, politicaPorcentaje);
        reservas.add(r);
        return r;
    }

    // Si choca con una reserva vigente se rechaza y se anota como conflicto de canal
    // Un mismo codigo externo no puede crear dos reservas
    public Reserva crearReservaExterna(String codigoExterno, Huesped titular, Apartamento apto, LocalDate entrada, LocalDate salida, ArrayList<Ocupante> acompanantes, LocalDateTime ahora) throws PadreException {
        if (codigoExterno == null || codigoExterno.trim().equals("")) {
            throw new DatoInvalidoException("El codigo externo es obligatorio.");
        }
        for (Reserva reserva : reservas) {
            if (reserva instanceof ReservaExterna) {
                ReservaExterna externa = (ReservaExterna) reserva;
                if (externa.getCodigoExterno().equalsIgnoreCase(codigoExterno.trim())) {
                    throw new ReglaNegocioException("El codigo externo " + codigoExterno + " ya creo la reserva " + reserva.getCodigo() + ".");
                }
            }
        }
        ArrayList<Ocupante> todos = new ArrayList<Ocupante>();
        Cotizacion cot;
        try {
            cot = validarReserva(titular, apto, entrada, salida, acompanantes, todos);
        } catch (PadreException exception) {
            // Solo el choque de noches lanza PadreException directo; las demás reglas lanzan una clase hija
            if (exception.getClass() == PadreException.class) {
                conflictos.add("Codigo externo " + codigoExterno + " rechazado: " + exception.getMessage());
            }
            throw exception;
        }
        Reserva reserva = new ReservaExterna(nuevoCodigo(), codigoExterno.trim(), apto, titular, todos, entrada, salida, ahora, cot,
                politicaDias, politicaHoras, politicaPorcentaje);
        reservas.add(reserva);
        return reserva;
    }

    public ArrayList<String> getConflictos() {
        return new ArrayList<String>(conflictos);
    }

    // Perfiles y empleados

    public void editarPerfil(Huesped huesped, String nombre, String telefono, String correo) throws PadreException {
        validarNumerico(telefono, "teléfono", 7, 10);
        huesped.editarDatos(nombre, telefono.trim(), correo);
    }

    public void editarRecepcionista(Recepcionista r, String nombre, String telefono, String correo) throws PadreException {
        if (!r.isActivo()) {
            throw new ReglaNegocioException("Un recepcionista desvinculado no se puede modificar.");
        }
        validarDatosRecepcionista(null, telefono, correo);
        r.editarDatos(nombre, telefono.trim(), correo.trim());
    }

    public void desvincularRecepcionista(Recepcionista r) throws PadreException {
        if (!r.isActivo()) {
            throw new ReglaNegocioException("El recepcionista ya estaba desvinculado.");
        }
        r.desvincular();
    }

    public void modificarApartamento(Apartamento apto, String nombre, int dormitorios, int capacidad, String caracteristicas, long baja, long media, long alta) throws PadreException {
        // No se puede bajar la capacidad por debajo de los ocupantes de una reserva activa
        for (Reserva reserva : reservas) {
            if (reserva.getApartamento() == apto && reserva.estaActiva() && reserva.getOcupantes().size() > capacidad) {
                throw new ReglaNegocioException("La reserva " + reserva.getCodigo() + " tiene mas personas que la nueva capacidad.");
            }
        }
        apto.modificar(nombre, dormitorios, capacidad, caracteristicas);
        apto.definirTarifas(baja, media, alta);
    }

    // Ciclo de vida de la reserva

    public void confirmarReserva(Reserva reserva, LocalDateTime ahora) throws PadreException {
        liberarVencidas(ahora);
        reserva.confirmar();
    }

    public void registrarLlegada(Reserva reserva, LocalDateTime ahora) throws PadreException {
        reserva.registrarLlegada(ahora);
    }

    public void registrarSalida(Reserva reserva, LocalDateTime ahora) throws PadreException {
        if (reserva.getEstado() == EstadoReserva.EN_CURSO && reserva.getFolio().saldo() > 0) {
            throw new ReglaNegocioException("No se puede hacer el check-out: la reserva tiene un saldo pendiente de "
                    + Formato.dinero(reserva.getFolio().saldo()) + ". Debe pagarse completo antes de la salida.");
        }
        reserva.registrarSalida(ahora);
    }

    public Liquidacion declararNoShow(Reserva reserva, LocalDateTime ahora) throws PadreException {
        return reserva.marcarNoShow(ahora);
    }

    // Correcciones

    public void corregirCargo(Reserva reserva, int posicion, String motivo, LocalDateTime ahora) throws PadreException {
        validarCorreccion(posicion, reserva.getFolio().getCargos().size(), motivo);
        reserva.getFolio().corregirCargo(posicion, motivo.trim(), ahora);
    }

    public void corregirPago(Reserva reserva, int posicion, String motivo, LocalDateTime ahora) throws PadreException {
        validarCorreccion(posicion, reserva.getFolio().getPagos().size(), motivo);
        reserva.getFolio().corregirPago(posicion, motivo.trim(), ahora);
    }

    private void validarCorreccion(int posicion, int cantidad, String motivo) throws PadreException {
        if (motivo == null || motivo.trim().equals("")) {
            throw new DatoInvalidoException("El motivo de la correccion es obligatorio.");
        }
        if (posicion < 0 || posicion >= cantidad) {
            throw new DatoInvalidoException("No existe un movimiento con ese numero.");
        }
    }

    // Operar reservas

    public ArrayList<Reserva> getReservas() {
        return new ArrayList<Reserva>(reservas);
    }

    public ArrayList<Reserva> getReservasDe(Huesped huesped) {
        ArrayList<Reserva> lista = new ArrayList<Reserva>();
        for (Reserva reserva : reservas) {
            if (reserva.getTitular() == huesped) {
                lista.add(reserva);
            }
        }
        return lista;
    }

    // Cancela las reservas pendientes que llevan 24 horas sin confirmarse
    public int liberarVencidas(LocalDateTime ahora) throws PadreException {
        int cantidad = 0;
        for (Reserva reserva : reservas) {
            if (reserva.plazoVencido(ahora)) {
                reserva.cancelarPorVencimiento(ahora);
                cantidad++;
            }
        }
        return cantidad;
    }

    // Pago o abono, si es del Portal y ya cubre el anticipo, la reserva se confirma sola.
    public void registrarPago(Reserva reserva, long monto, MedioPago medio, LocalDateTime ahora) throws PadreException {
        liberarVencidas(ahora);
        EstadoReserva estado = reserva.getEstado();
        if (estado == EstadoReserva.FINALIZADA) {
            throw new ReglaNegocioException("La reserva está FINALIZADA y no admite más pagos.");
        }
        // Cancelada o no-show: solo se puede pagar la penalidad que haya quedado pendiente
        if ((estado == EstadoReserva.CANCELADA || estado == EstadoReserva.NO_SHOW) && reserva.getFolio().saldo() <= 0) {
            throw new ReglaNegocioException("La reserva está " + estado + " y no tiene saldo pendiente: no admite pagos.");
        }
        if (monto > reserva.getFolio().saldo()) {
            throw new ReglaNegocioException("El pago supera el saldo pendiente (" + Formato.dinero(reserva.getFolio().saldo()) + ").");
        }
        reserva.getFolio().agregarPago(monto, medio, ahora);
        if (reserva instanceof ReservaPortal && reserva.getEstado() == EstadoReserva.PENDIENTE && reserva.anticipoCubierto()) {
            reserva.confirmar();
        }
    }

    public Liquidacion cancelarReserva(Reserva reserva, LocalDateTime ahora) throws PadreException {
        liberarVencidas(ahora);
        return reserva.cancelar(ahora);
    }

    public void registrarServicio(Reserva reserva, String concepto, long valor, LocalDateTime ahora) throws PadreException {
        if (!reserva.estaActiva()) {
            throw new ReglaNegocioException("La reserva esta " + reserva.getEstado() + " y no admite nuevos cargos.");
        }
        if (concepto == null || concepto.trim().equals("")) {
            throw new DatoInvalidoException("El concepto del servicio es obligatorio.");
        }
        reserva.getFolio().agregarCargo(concepto.trim(), valor, ahora);
    }

    // Reportes

    public String reporteOcupacion(LocalDate desde, LocalDate hasta) throws PadreException {
        if (hasta.isBefore(desde)) {
            throw new DatoInvalidoException("El periodo no es valido.");
        }
        int nochesPeriodo = (int) (hasta.toEpochDay() - desde.toEpochDay()) + 1;
        String texto = "OCUPACION POR APARTAMENTO (" + desde + " a " + hasta + ")\n";
        for (Apartamento apartamento : getApartamentos()) {
            int ocupadas = 0;
            for (Reserva reserva : reservas) {
                if (reserva.getApartamento() == apartamento && reserva.generaOcupacion()) {
                    LocalDate noche = desde;
                    while (!noche.isAfter(hasta)) {
                        if (reserva.ocupaNoche(noche)) {
                            ocupadas++;
                        }
                        noche = noche.plusDays(1);
                    }
                }
            }
            double porcentaje = 100.0 * ocupadas / nochesPeriodo;
            texto += apartamento.getCodigo() + " " + apartamento.getNombre() + ": " + ocupadas + " de " + nochesPeriodo
                    + " noches (" + String.format(java.util.Locale.US, "%.1f", porcentaje) + "%)\n";
        }
        return texto;
    }

    // Ingresos = pagos recibidos en el periodo
    // Comisiones = de las externas no canceladas con entrada en el periodo
    public String reporteIngresos(LocalDate desde, LocalDate hasta) throws PadreException {
        if (hasta.isBefore(desde)) {
            throw new DatoInvalidoException("El periodo no es valido.");
        }
        long ingresos = 0;
        long comisiones = 0;
        for (Reserva reserva : reservas) {
            for (Movimiento pagos : reserva.getFolio().getPagos()) {
                LocalDate dia = pagos.getFecha().toLocalDate();
                if (!dia.isBefore(desde) && !dia.isAfter(hasta)) {
                    ingresos = ingresos + pagos.getValor();
                }
            }
            if (reserva.getEstado() != EstadoReserva.CANCELADA && !reserva.getEntrada().isBefore(desde) && !reserva.getEntrada().isAfter(hasta)) {
                comisiones = comisiones + reserva.calcularComision();
            }
        }
        return "INGRESOS (" + desde + " a " + hasta + ")\nPagos netos recibidos: " + Formato.dinero(ingresos)
                + "\nComisiones a plataformas: " + Formato.dinero(comisiones)
                + "\nIngresos netos: " + Formato.dinero(ingresos - comisiones) + "\n";
    }

    // Cuenta las reservas de cada canal, incluidas las canceladas y los no-show
    public String reportePorCanal() {
        String[] canales = {"PORTAL", "DIRECTA", "EXTERNA"};
        String texto = "RESERVAS POR CANAL\n";
        for (String canal : canales) {
            int total = 0;
            int canceladas = 0;
            int noShow = 0;
            for (Reserva reserva : reservas) {
                if (reserva.getCanal().equals(canal)) {
                    total = total + 1;
                    if (reserva.getEstado() == EstadoReserva.CANCELADA) {
                        canceladas = canceladas + 1;
                    }
                    if (reserva.getEstado() == EstadoReserva.NO_SHOW) {
                        noShow = noShow + 1;
                    }
                }
            }
            texto += canal + ": " + total + " reservas, " + canceladas + " canceladas, " + noShow + " no-show\n";
        }
        return texto;
    }

    public String reporteSaldos() {
        String texto = "SALDOS PENDIENTES\n";
        for (Reserva reserva : reservas) {
            if (reserva.getFolio().saldo() > 0) {
                texto += reserva.getCodigo() + " | " + reserva.getTitular().getNombre() + " | " + reserva.getEstado() + " | " + Formato.dinero(reserva.getFolio().saldo()) + "\n";
            }
        }
        return texto;
    }

    // El apartamento con más novedades sale primero
    public String reporteNovedades() {
        String texto = "NOVEDADES POR APARTAMENTO\n";
        ArrayList<Apartamento> pendientes = new ArrayList<Apartamento>(apartamentos);
        while (pendientes.size() > 0) {
            Apartamento mayor = pendientes.get(0);
            for (Apartamento apartamento : pendientes) {
                if (contarNovedades(apartamento) > contarNovedades(mayor)) {
                    mayor = apartamento;
                }
            }
            pendientes.remove(mayor);
            int cantidad = contarNovedades(mayor);
            if (cantidad > 0) {
                texto += mayor.getCodigo() + " " + mayor.getNombre() + ": " + cantidad + " novedades\n";
            }
        }
        return texto;
    }

    private int contarNovedades(Apartamento apto) {
        int cantidad = 0;
        for (Novedad novedad : novedades) {
            if (novedad.getApartamento() == apto) {
                cantidad = cantidad + 1;
            }
        }
        return cantidad;
    }
}