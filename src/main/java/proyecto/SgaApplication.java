package proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import proyecto.model.*;

import java.io.IOException;

// Guarda el modelo y el usuario que inicio sesión, y cambia de pantalla
public class SgaApplication extends Application {

    private static Alojamiento modelo = new Alojamiento();
    private static Usuario usuarioActual;
    private static Stage ventana;

    @Override
    public void start(Stage stage) throws IOException {
        ventana = stage;
        ventana.setTitle("SGA - Aparta-Suites UQ");
        try {
            modelo.cargarDatosDeEjemplo();
        } catch (Exception e) {
            System.out.println("No se pudieron cargar los datos de ejemplo: " + e.getMessage());
        }
        mostrarPantalla("login-view.fxml", 650, 500);
        ventana.show();
    }

    public static void mostrarPantalla(String archivoFxml, double ancho, double alto) throws IOException {
        FXMLLoader loader = new FXMLLoader(SgaApplication.class.getResource(archivoFxml));
        Scene escena = new Scene(loader.load(), ancho, alto);
        ventana.setScene(escena);
    }

    public static Alojamiento getModelo() {
        return modelo;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void main(String[] args) {
        launch(args);
    }
}