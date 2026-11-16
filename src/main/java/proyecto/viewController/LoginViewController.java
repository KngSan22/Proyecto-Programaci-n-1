package proyecto.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import proyecto.SgaApplication;
import proyecto.model.*;

public class LoginViewController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNacimiento;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtNuevoUsuario;
    @FXML private PasswordField txtNuevaClave;

    private Alojamiento modelo = SgaApplication.getModelo();

    @FXML
    private void onIngresar() {
        try {
            Usuario u = modelo.iniciarSesion(txtUsuario.getText().trim(), txtClave.getText());
            SgaApplication.setUsuarioActual(u);
            // Segun el rol se muestra una pantalla distinta
            if (u.getRol().equals("HUÉSPED")) {
                SgaApplication.mostrarPantalla("huesped-view.fxml", 950, 700);
            } else if (u.getRol().equals("RECEPCIONISTA")) {
                SgaApplication.mostrarPantalla("recepcion-view.fxml", 1000, 700);
            } else {
                SgaApplication.mostrarPantalla("admin-view.fxml", 1000, 700);
            }
        } catch (Exception ex) {
            Util.error(ex);
        }
    }

    @FXML
    private void onRegistrar() {
        try {
            modelo.registrarHuesped(txtNombre.getText(), txtDocumento.getText(), Util.fecha(txtNacimiento.getText()),
                    txtTelefono.getText(), txtCorreo.getText(), txtNuevoUsuario.getText(), txtNuevaClave.getText());
            Util.mensaje("Perfil creado. Ya puede ingresar con su usuario y contraseña.");
        } catch (Exception ex) {
            Util.error(ex);
        }
    }
}