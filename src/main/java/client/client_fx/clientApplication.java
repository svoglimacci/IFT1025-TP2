package client.client_fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/*
 * @author Simon Voglimacci Stephanopoli    20002825
 * @author Victor Leblond 20244841
 * @version 1.0
 * @since 2023-03-30
 */


/**
 * La classe clientApplication contient la fonction {@link #main(String[] args)} et lance l'application
 */
public class clientApplication extends Application {

    public static void main(String[] args) throws IOException {

        launch(args);
    }

    /**
     * La fonction start 'load' le fichier FXML et lance l'application
     * @param stage
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {
        URL fxmlURL = getClass().getResource("/client-view.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(fxmlURL);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root,600,400);
        stage.setScene(scene);
        stage.show();
    }
}
