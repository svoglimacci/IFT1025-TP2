package client.client_fx;

import client.Client;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import server.models.Course;

import java.io.IOException;

/*
 * @author Simon Voglimacci Stephanopoli    20002825
 * @author Victor Leblond 20244841
 * @version 1.0
 * @since 2023-03-30
 */


/**
 * La classe clientController s'occupe de la logique derrière les éléments FXML du GUI
 */
public class clientController {

    /*
    * Références aux éléments présent dans le GUI
     */
    @FXML private ComboBox boxSession;
    @FXML private TableView<Course> tableCours;
    @FXML private TableColumn<Course, String> codeColone;
    @FXML private TableColumn<Course, String> nomColone;

    @FXML private TextField prenomField;
    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private TextField matriculeField;

    private final static int PORT = 1337;
    private final static String HOST_NAME = "127.0.0.1";

    private Client client;

    /**
     * La fonction est appelé lorsque le fichier FXML est bien 'loadé' par clientApplication. La fonction crée un client et
     * 'set' la propriété des colonnes du tableau des cours.
     */
    @FXML
    public void initialize() {
        try {
            client = new Client(HOST_NAME,PORT);
            codeColone.setCellValueFactory(new PropertyValueFactory<Course, String>("code"));
            nomColone.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * La fonction chargerSession rafraîchit le TableView des cours avec les cours de cours.txt
     * @throws IOException
     */
    public void chargerSession() throws IOException {
        String currentSession = boxSession.getSelectionModel().selectedItemProperty().getValue().toString();

        tableCours.getItems().clear();
        tableCours.setItems(FXCollections.observableList(client.loadCourses(currentSession)));
        tableCours.refresh();
    }

    /**
    *   La fonction inscrireCours prend en compte les TextFields ainsi que le cours sélectionné
     *  et incrit le client à son cours. La fonction affiche une alerte soit d'érreure ou d'information
     *  dépendament de si l'inscription est réussite ou non.
     *  @throws IOException
     *  @throws ClassNotFoundException
     */
    public void inscrireCours() throws IOException, ClassNotFoundException {
        String prenomText = prenomField.getText();
        String nomText = nomField.getText();
        String emailText = emailField.getText();
        String matriculeText = matriculeField.getText();

        Course selectedCourse = tableCours.getSelectionModel().getSelectedItem();

        String output = client.register(prenomText,nomText,emailText,matriculeText,selectedCourse);
        Alert alert = new Alert(Alert.AlertType.NONE);
        if(output.contains("Félicitations!")){
            alert.setAlertType(Alert.AlertType.INFORMATION);

            prenomField.clear();
            nomField.clear();
            emailField.clear();
            matriculeField.clear();
        }else{
            alert.setAlertType(Alert.AlertType.ERROR);
        }

        alert.setTitle("Message");
        alert.setHeaderText("Message");
        alert.setContentText(output);
        alert.showAndWait();
    }

}
