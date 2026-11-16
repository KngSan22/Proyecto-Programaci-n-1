package proyecto.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import proyecto.SgaApplication;
import proyecto.exception.*;
import proyecto.model.*;

// Controlador de admin-view.fxml
public class AdminViewController {

    @FXML private Label lblBienvenida;

    // Recepcionistas
    @FXML private ListView<Recepcionista> lstRecepcionistas;
    @FXML private TextField txtRNombre;
    @FXML private TextField txtRDocumento;
    @FXML private TextField txtRTelefono;
    @FXML private TextField txtRCorreo;
    @FXML private TextField txtRUsuario;
    @FXML private PasswordField txtRClave;

    // Apartamentos
    @FXML private ListView<Apartamento> lstApartamentos;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDormitorios;
    @FXML private TextField txtCapacidad;
    @FXML private TextField txtCaracteristicas;
    @FXML private TextField txtTarifaBaja;
    @FXML private TextField txtTarifaMedia;
    @FXML private TextField txtTarifaAlta;

    // Temporadas y politica
    @FXML private ComboBox<Temporada> cmbTemporada;
    @FXML private TextField txtPeriodoDesde;
    @FXML private TextField txtPeriodoHasta;
    @FXML private ListView<PeriodoTemporada> lstPeriodos;
    @FXML private TextField txtDias;
    @FXML private TextField txtHoras;
    @FXML private TextField txtPorcentaje;

    // Bloqueos
    @FXML private ComboBox<Apartamento> cmbApartamento;
    @FXML private TextField txtBloqueoDesde;
    @FXML private TextField txtBloqueoHasta;
    @FXML private TextField txtBloqueoMotivo;
    @FXML private ListView<Bloqueo> lstBloqueos;

    // Reportes
    @FXML private TextField txtReporteDesde;
    @FXML private TextField txtReporteHasta;
    @FXML private TextArea txtReporte;

    private Alojamiento modelo = SgaApplication.getModelo();

    @FXML
    private void initialize() {
        lblBienvenida.setText("Administrador: " + SgaApplication.getUsuarioActual().getNombre());
        cmbTemporada.getItems().setAll(Temporada.MEDIA, Temporada.ALTA);
        cmbTemporada.setValue(Temporada.ALTA);
        // Al elegir un apartamento de la lista se llenan los campos para poder modificarlo
        lstApartamentos.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> llenarCampos(nuevo));
        actualizarTodo();
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

    private void actualizarTodo() {
        lstRecepcionistas.getItems().setAll(modelo.getRecepcionistas());
        lstApartamentos.getItems().setAll(modelo.getApartamentos());
        cmbApartamento.getItems().setAll(modelo.getApartamentos());
        lstPeriodos.getItems().setAll(modelo.getPeriodos());
        lstBloqueos.getItems().setAll(modelo.getBloqueos());
        txtDias.setText("" + modelo.getPoliticaDias());
        txtHoras.setText("" + modelo.getPoliticaHoras());
        txtPorcentaje.setText("" + modelo.getPoliticaPorcentaje());
    }

    // ---------- Recepcionistas ----------

    @FXML
    private void onContratar() {
        try {
            modelo.contratarRecepcionista(txtRNombre.getText(), txtRDocumento.getText(), txtRTelefono.getText(),
                    txtRCorreo.getText(), txtRUsuario.getText(), txtRClave.getText());
            actualizarTodo();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    private Recepcionista recepcionistaElegido() throws PadreException {
        Recepcionista r = lstRecepcionistas.getSelectionModel().getSelectedItem();
        if (r == null) {
            throw new ReglaNegocioException("Seleccione un recepcionista de la lista.");
        }
        return r;
    }

    @FXML
    private void onEditarRecepcionista() {
        try {
            modelo.editarRecepcionista(recepcionistaElegido(), txtRNombre.getText(), txtRTelefono.getText(), txtRCorreo.getText());
            actualizarTodo();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onDesvincular() {
        try {
            modelo.desvincularRecepcionista(recepcionistaElegido());
            actualizarTodo();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    // Apartamentos

    private void llenarCampos(Apartamento a) {
        if (a == null) {
            return;
        }
        txtCodigo.setText(a.getCodigo());
        txtNombre.setText(a.getNombre());
        txtDormitorios.setText("" + a.getDormitorios());
        txtCapacidad.setText("" + a.getCapacidad());
        txtCaracteristicas.setText(a.getCaracteristicas());
        txtTarifaBaja.setText("" + a.tarifaDe(Temporada.BAJA));
        txtTarifaMedia.setText("" + a.tarifaDe(Temporada.MEDIA));
        txtTarifaAlta.setText("" + a.tarifaDe(Temporada.ALTA));
    }

    @FXML
    private void onCrearApartamento() {
        try {
            modelo.crearApartamento(txtCodigo.getText(), txtNombre.getText(), (int) Util.numero(txtDormitorios.getText()),
                    (int) Util.numero(txtCapacidad.getText()), txtCaracteristicas.getText(), Util.numero(txtTarifaBaja.getText()),
                    Util.numero(txtTarifaMedia.getText()), Util.numero(txtTarifaAlta.getText()));
            actualizarTodo();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    private Apartamento apartamentoElegido() throws PadreException {
        Apartamento a = lstApartamentos.getSelectionModel().getSelectedItem();
        if (a == null) {
            throw new ReglaNegocioException("Seleccione un apartamento de la lista.");
        }
        return a;
    }

    @FXML
    private void onModificarApartamento() {
        try {
            modelo.modificarApartamento(apartamentoElegido(), txtNombre.getText(), (int) Util.numero(txtDormitorios.getText()),
                    (int) Util.numero(txtCapacidad.getText()), txtCaracteristicas.getText(), Util.numero(txtTarifaBaja.getText()),
                    Util.numero(txtTarifaMedia.getText()), Util.numero(txtTarifaAlta.getText()));
            actualizarTodo();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onEliminarApartamento() {
        try {
            modelo.eliminarApartamento(apartamentoElegido());
            actualizarTodo();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    //Temporadas y política
    @FXML
    private void onAgregarPeriodo() {
        try {
            modelo.agregarPeriodo(cmbTemporada.getValue(), Util.fecha(txtPeriodoDesde.getText()), Util.fecha(txtPeriodoHasta.getText()));
            actualizarTodo();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onEliminarPeriodo() {
        PeriodoTemporada p = lstPeriodos.getSelectionModel().getSelectedItem();
        if (p != null) {
            modelo.eliminarPeriodo(p);
            actualizarTodo();
        }
    }

    @FXML
    private void onGuardarPolitica() {
        try {
            modelo.configurarPolitica((int) Util.numero(txtDias.getText()), (int) Util.numero(txtHoras.getText()),
                    (int) Util.numero(txtPorcentaje.getText()));
            Util.mensaje("Política guardada. Solo aplica a reservas nuevas.");
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    // ---------- Bloqueos ----------

    @FXML
    private void onBloquear() {
        try {
            Apartamento a = cmbApartamento.getValue();
            if (a == null) {
                throw new ReglaNegocioException("Seleccione un apartamento.");
            }
            modelo.registrarBloqueo(a, Util.fecha(txtBloqueoDesde.getText()), Util.fecha(txtBloqueoHasta.getText()), txtBloqueoMotivo.getText());
            actualizarTodo();
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onEliminarBloqueo() {
        Bloqueo b = lstBloqueos.getSelectionModel().getSelectedItem();
        if (b != null) {
            modelo.eliminarBloqueo(b);
            actualizarTodo();
        }
    }

    // Reportes

    @FXML
    private void onReporteOcupacion() {
        try {
            txtReporte.setText(modelo.reporteOcupacion(Util.fecha(txtReporteDesde.getText()), Util.fecha(txtReporteHasta.getText())));
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onReporteIngresos() {
        try {
            txtReporte.setText(modelo.reporteIngresos(Util.fecha(txtReporteDesde.getText()), Util.fecha(txtReporteHasta.getText())));
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onReporteCanales() {
        txtReporte.setText(modelo.reportePorCanal());
    }

    @FXML
    private void onReporteSaldos() {
        txtReporte.setText(modelo.reporteSaldos());
    }

    @FXML
    private void onReporteNovedades() {
        txtReporte.setText(modelo.reporteNovedades());
    }
}