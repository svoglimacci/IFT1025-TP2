package client;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import server.models.Course;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class clientApplication extends Application {

    public static Socket createSocket(String hostName, int port) throws IOException {
        return new Socket(hostName, port);
    }

    public static void main(String[] args) throws IOException {

        launch();
    }
    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {
        URL fxmlURL = getClass().getResource("client-view.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);

        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root,600,400);

        stage.setScene(scene);
        stage.show();
    }
}
