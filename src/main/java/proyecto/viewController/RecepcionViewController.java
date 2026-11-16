package proyecto.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import proyecto.SgaApplication;
import proyecto.exception.*;
import proyecto.model.*;

import java.time.LocalDateTime;

// Controlador de recepcion-view.fxml
public class RecepcionViewController {

    @FXML private Label lblBienvenida;

    // Reservas
    @FXML private ListView<Reserva> lstReservas;
    @FXML private TextField txtMonto;
    @FXML private ComboBox<MedioPago> cmbMedio;
    @FXML private TextField txtConcepto;
    @FXML private TextField txtValorServicio;
    @FXML private TextField txtNumeroMovimiento;
    @FXML private TextField txtMotivoCorreccion;
    @FXML private TextArea txtDetalle;

    // Nueva Reserva
    @FXML private TextField txtDocumentoTitular;
    @FXML private ComboBox<Apartamento> cmbApartamento;
    @FXML private TextField txtEntrada;
    @FXML private TextField txtSalida;
    @FXML private TextArea txtAcompanantes;
    @FXML private TextField txtCodigoExterno;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNacimiento;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;

    // Apartamentos
    @FXML private ListView<Apartamento> lstApartamentos;
    @FXML private ComboBox<EstadoApartamento> cmbEstado;
    @FXML private TextField txtMotivo;
    @FXML private TextField txtNovedad;
    @FXML private ListView<String> lstNovedades;
    @FXML private ListView<String> lstConflictos;

    // Huespedes
    @FXML private ListView<String> lstHuespedes;

    private Alojamiento modelo = SgaApplication.getModelo();

    @FXML
    private void initialize() {
        lblBienvenida.setText("Recepcionista: " + SgaApplication.getUsuarioActual().getNombre());
        cmbMedio.getItems().setAll(MedioPago.values());
        cmbMedio.setValue(MedioPago.EFECTIVO);
        // OCUPADO no se ofrece: solo lo pone el check-in
        cmbEstado.getItems().setAll(EstadoApartamento.PREPARADO, EstadoApartamento.PENDIENTE_PREPARACION, EstadoApartamento.EN_PREPARACION, EstadoApartamento.FUERA_DE_SERVICIO);
        onActualizar();
    }

    @FXML
    private void onSalir() {
        try {
            SgaApplication.setUsuarioActual(null);
            SgaApplication.mostrarPantalla("login-view.fxml", 650, 500);
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onActualizar() {
        try {
            modelo.liberarVencidas(LocalDateTime.now());
            // Al refrescar la lista se conserva la reserva seleccionada
            Reserva elegida = lstReservas.getSelectionModel().getSelectedItem();
            lstReservas.getItems().setAll(modelo.getReservas());
            if (elegida != null) {
                lstReservas.getSelectionModel().select(elegida);
            }
            lstApartamentos.getItems().setAll(modelo.getApartamentos());
            cmbApartamento.getItems().setAll(modelo.getApartamentos());
            lstNovedades.getItems().clear();
            for (Novedad novedad : modelo.getNovedades()) {
                lstNovedades.getItems().add(novedad.toString());
            }
            lstConflictos.getItems().setAll(modelo.getConflictos());
            actualizarHuespedes();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    private Reserva reservaElegida() throws PadreException {
        Reserva reserva = lstReservas.getSelectionModel().getSelectedItem();
        if (reserva == null) {
            throw new ReglaNegocioException("Seleccione una reserva de la lista.");
        }
        return reserva;
    }

    @FXML
    private void onConfirmar() {
        try {
            modelo.confirmarReserva(reservaElegida(), LocalDateTime.now());
            onActualizar();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onCheckIn() {
        try {
            modelo.registrarLlegada(reservaElegida(), LocalDateTime.now());
            onActualizar();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onCheckOut() {
        try {
            modelo.registrarSalida(reservaElegida(), LocalDateTime.now());
            onActualizar();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onNoShow() {
        try {
            Liquidacion liq = modelo.declararNoShow(reservaElegida(), LocalDateTime.now());
            Util.mensaje("Reserva declarada no-show.\n" + liq.resumen());
            onActualizar();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onCancelar() {
        try {
            Reserva reserva = reservaElegida();
            if (!reserva.puedeCancelarse()) {
                throw new ReglaNegocioException("Esa reserva esta " + reserva.getEstado() + " y no se puede cancelar.");
            }
            Liquidacion liq = reserva.calcularLiquidacion(LocalDateTime.now());
            if (Util.confirmar("Si se cancela ahora:\n" + liq.resumen() + "\n\nCancelar la reserva?")) {
                modelo.cancelarReserva(reserva, LocalDateTime.now());
                onActualizar();
            }
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    // Muestra cargos y pagos con su número
    @FXML
    private void onVerFolio() {
        try {
            Reserva reserva = reservaElegida();
            String texto = "CARGOS:\n";
            int i = 0;
            for (Movimiento mov : reserva.getFolio().getCargos()) {
                texto += i + ": " + mov + "\n";
                i += 1;
            }
            texto += "\nPAGOS:\n";
            i = 0;
            for (Movimiento mov : reserva.getFolio().getPagos()) {
                texto += i + ": " + mov + "\n";
                i += 1;
            }
            texto = texto + "\nSALDO: " + Formato.dinero(reserva.getFolio().saldo());
            txtDetalle.setText(texto);
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onPagar() {
        try {
            modelo.registrarPago(reservaElegida(), Util.numero(txtMonto.getText()), cmbMedio.getValue(), LocalDateTime.now());
            onActualizar();
            onVerFolio();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onServicio() {
        try {
            modelo.registrarServicio(reservaElegida(), txtConcepto.getText(), Util.numero(txtValorServicio.getText()), LocalDateTime.now());
            onActualizar();
            onVerFolio();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    // Los cargos y pagos no se editan
    private void corregir(boolean esCargo) {
        try {
            Reserva reserva = reservaElegida();
            int posicion = (int) Util.numero(txtNumeroMovimiento.getText());
            String motivo = txtMotivoCorreccion.getText();
            if (esCargo) {
                modelo.corregirCargo(reserva, posicion, motivo, LocalDateTime.now());
            } else {
                modelo.corregirPago(reserva, posicion, motivo, LocalDateTime.now());
            }
            onActualizar();
            onVerFolio();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onCorregirCargo() {
        corregir(true);
    }

    @FXML
    private void onCorregirPago() {
        corregir(false);
    }

    // Si escribe código externo la reserva es externa; si lo deja vacío es directa
    @FXML
    private void onCrearReserva() {
        try {
            Huesped titular = modelo.buscarHuesped(txtDocumentoTitular.getText());
            Apartamento apto = cmbApartamento.getValue();
            if (apto == null) {
                throw new ReglaNegocioException("Seleccione un apartamento.");
            }
            Reserva reserva;
            if (txtCodigoExterno.getText().trim().equals("")) {
                reserva = modelo.crearReservaDirecta(titular, apto, Util.fecha(txtEntrada.getText()), Util.fecha(txtSalida.getText()), Util.ocupantes(txtAcompanantes.getText()), LocalDateTime.now());
            } else {
                reserva = modelo.crearReservaExterna(txtCodigoExterno.getText(), titular, apto, Util.fecha(txtEntrada.getText()), Util.fecha(txtSalida.getText()), Util.ocupantes(txtAcompanantes.getText()), LocalDateTime.now());
            }
            Util.mensaje("Reserva " + reserva.getCodigo() + " creada (" + reserva.getCanal() + "). Valor: " + Formato.dinero(reserva.getValorEstancia()));
            onActualizar();
        } catch (Exception ex) {
            onActualizar();
            Util.error(ex);
        }
    }

    @FXML
    private void onRegistrarHuesped() {
        try {
            modelo.registrarHuesped(txtNombre.getText(), txtDocumento.getText(), Util.fecha(txtNacimiento.getText()), txtTelefono.getText(), txtCorreo.getText(), txtUsuario.getText(), txtClave.getText());
            Util.mensaje("Huésped registrado.");
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onCambiarEstado() {
        try {
            Apartamento apartamento = lstApartamentos.getSelectionModel().getSelectedItem();
            if (apartamento == null || cmbEstado.getValue() == null) {
                throw new ReglaNegocioException("Seleccione un apartamento y el nuevo estado.");
            }
            modelo.cambiarEstadoApartamento(apartamento, cmbEstado.getValue(), txtMotivo.getText());
            onActualizar();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onNovedad() {
        try {
            Apartamento apartamento = lstApartamentos.getSelectionModel().getSelectedItem();
            if (apartamento == null) {
                throw new ReglaNegocioException("Seleccione un apartamento.");
            }
            modelo.registrarNovedad(apartamento, txtNovedad.getText(), LocalDateTime.now());
            txtNovedad.clear();
            onActualizar();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }
    private void actualizarHuespedes() {
        lstHuespedes.getItems().clear();
        for (Huesped h : modelo.getHuespedes()) {
            lstHuespedes.getItems().add(Util.datosHuesped(h));
        }
    }
}