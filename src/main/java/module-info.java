module proyecto {
    requires javafx.controls;
    requires javafx.fxml;

    opens proyecto to javafx.fxml;
    exports proyecto;
    exports proyecto.model;
    exports proyecto.exception;
}