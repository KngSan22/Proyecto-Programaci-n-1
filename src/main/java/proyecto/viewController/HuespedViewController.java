package proyecto.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import proyecto.SgaApplication;
import proyecto.exception.PadreException;
import proyecto.exception.ReglaNegocioException;
import proyecto.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

// Controlador de huesped-view.fxml
public class HuespedViewController {

    @FXML private Label lblBienvenida;
    @FXML private TextField txtEntrada;
    @FXML private TextField txtSalida;
    @FXML private TextField txtPersonas;
    @FXML private ListView<Apartamento> lstApartamentos;
    @FXML private TextArea txtAcompanantes;
    @FXML private TextArea txtCotizacion;
    @FXML private ListView<Reserva> lstReservas;
    @FXML private TextField txtMonto;
    @FXML private ComboBox<MedioPago> cmbMedio;
    @FXML private TextArea txtDetalle;
    @FXML private TextField txtPerfilNombre;
    @FXML private TextField txtPerfilTelefono;
    @FXML private TextField txtPerfilCorreo;

    private Alojamiento modelo = SgaApplication.getModelo();
    private Huesped huesped = (Huesped) SgaApplication.getUsuarioActual();

    // Carga de pantalla
    @FXML
    private void initialize() {
        lblBienvenida.setText("Huesped: " + huesped.getNombre());
        txtPersonas.setText("2");
        cmbMedio.getItems().setAll(MedioPago.values());
        cmbMedio.setValue(MedioPago.EFECTIVO);
        mostrarPerfil();
        onActualizar();
    }

    private void mostrarPerfil() {
        txtPerfilNombre.setText(huesped.getNombre());
        txtPerfilTelefono.setText(huesped.getTelefono());
        txtPerfilCorreo.setText(huesped.getCorreo());
    }

    @FXML
    private void onGuardarPerfil() {
        try {
            modelo.editarPerfil(huesped, txtPerfilNombre.getText(), txtPerfilTelefono.getText(), txtPerfilCorreo.getText());
            lblBienvenida.setText("Huesped: " + huesped.getNombre());
            Util.mensaje("Perfil actualizado.");
        } catch (Exception ex) {
            mostrarPerfil();
            Util.error(ex);
        }
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
    private void onBuscar() {
        try {
            ArrayList<Apartamento> libres = modelo.buscarDisponibles(Util.fecha(txtEntrada.getText()),
                    Util.fecha(txtSalida.getText()), (int) Util.numero(txtPersonas.getText()));
            lstApartamentos.getItems().setAll(libres);
            if (libres.size() == 0) {
                Util.mensaje("No hay apartamentos disponibles para esas fechas.");
            }
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    // Lista de ocupantes, huesped + acompañantes
    private ArrayList<Ocupante> todosLosOcupantes() throws PadreException {
        ArrayList<Ocupante> todos = new ArrayList<Ocupante>();
        todos.add(new Ocupante(huesped.getNombre(), huesped.getFechaNacimiento()));
        todos.addAll(Util.ocupantes(txtAcompanantes.getText()));
        return todos;
    }

    private Apartamento apartamentoElegido() throws PadreException {
        Apartamento apto = lstApartamentos.getSelectionModel().getSelectedItem();
        if (apto == null) {
            throw new ReglaNegocioException("Seleccione un apartamento de la lista.");
        }
        return apto;
    }

    @FXML
    private void onCotizar() {
        try {
            Cotizacion c = modelo.cotizar(apartamentoElegido(), Util.fecha(txtEntrada.getText()),
                    Util.fecha(txtSalida.getText()), todosLosOcupantes());
            txtCotizacion.setText(c.resumen() + "\nAnticipo del 30%: " + Formato.dinero(c.getTotal() * 30 / 100));
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onReservar() {
        try {
            Reserva r = modelo.crearReservaPortal(huesped, apartamentoElegido(), Util.fecha(txtEntrada.getText()),
                    Util.fecha(txtSalida.getText()), Util.ocupantes(txtAcompanantes.getText()), LocalDateTime.now());
            Util.mensaje("Reserva " + r.getCodigo() + " creada. Queda PENDIENTE: pague el anticipo de "
                    + Formato.dinero(r.calcularAnticipo()) + " en las proximas 24 horas desde -Mis reservas-");
            onActualizar();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onActualizar() {
        try {
            modelo.liberarVencidas(LocalDateTime.now());
            lstReservas.getItems().setAll(modelo.getReservasDe(huesped));
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    private Reserva reservaElegida() throws PadreException {
        Reserva r = lstReservas.getSelectionModel().getSelectedItem();
        if (r == null) {
            throw new ReglaNegocioException("Seleccione una reserva de la lista.");
        }
        return r;
    }

    @FXML
    private void onVerFolio() {
        try {
            Reserva r = reservaElegida();
            String texto = "CARGOS:\n";
            for (Movimiento m : r.getFolio().getCargos()) {
                texto += m + "\n";
            }
            texto = texto + "\nPAGOS:\n";
            for (Movimiento m : r.getFolio().getPagos()) {
                texto += m + "\n";
            }
            texto += "\nSALDO: " + Formato.dinero(r.getFolio().saldo());
            txtDetalle.setText(texto);
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onPagar() {
        try {
            Reserva reserva = reservaElegida();
            modelo.registrarPago(reserva, Util.numero(txtMonto.getText()), cmbMedio.getValue(), LocalDateTime.now());
            onActualizar();
            Util.mensaje("Pago registrado. Estado de la reserva: " + reserva.getEstado());
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onVerCancelacion() {
        try {
            Reserva reserva = reservaElegida();
            if (!reserva.puedeCancelarse()) {
                throw new ReglaNegocioException("Esa reserva esta " + reserva.getEstado() + " y no se puede cancelar.");
            }
            txtDetalle.setText("Si cancela ahora:\n" + reserva.calcularLiquidacion(LocalDateTime.now()).resumen());
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
            if (Util.confirmar("Si cancela ahora:\n" + liq.resumen() + "\n\nDesea cancelar la reserva?")) {
                modelo.cancelarReserva(reserva, LocalDateTime.now());
                onActualizar();
            }
        } catch (Exception ex) {
            Util.error(ex);
        }
    }
}