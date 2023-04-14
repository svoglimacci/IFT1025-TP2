module Server {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens server.models to javafx.fxml;

    opens client.client_fx to javafx.fxml;
    exports client.client_fx;
    exports server.models;

}

