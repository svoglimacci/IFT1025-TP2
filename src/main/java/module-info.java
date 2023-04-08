module Server {
    requires javafx.controls;
    requires javafx.fxml;

    opens server.models to javafx.fxml;

    opens client_fx to javafx.fxml;
    exports client_fx;
    exports server.models;
}