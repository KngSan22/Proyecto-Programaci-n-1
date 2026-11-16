module proyecto {
    requires javafx.controls;
    requires javafx.fxml;

    opens proyecto to javafx.fxml;
    exports proyecto;
    exports proyecto.model;
    exports proyecto.exception;

    opens proyecto.viewController to javafx.fxml;
    exports proyecto.viewController;
}