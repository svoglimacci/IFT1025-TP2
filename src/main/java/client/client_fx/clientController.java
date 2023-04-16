package client.client_fx;

import client.Client;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Popup;
import server.models.Course;

import java.io.IOException;

public class clientController {
    @FXML private ComboBox boxSession;
    @FXML private Button chargerButton;

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

    //@FXML private Label
    @FXML
    public void initialize() {
        try {
            client = new Client(HOST_NAME,PORT);
            run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run()throws IOException{
        codeColone.setCellValueFactory(new PropertyValueFactory<Course, String>("code"));
        nomColone.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
        boxSession.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> selected, String oldFruit, String newFruit) {

            }
        });
    }

    public void chargerSession() throws IOException {
        String currentSession = boxSession.getSelectionModel().selectedItemProperty().getValue().toString();

        tableCours.getItems().clear();
        tableCours.setItems(FXCollections.observableList(client.loadCourses(currentSession)));
        tableCours.refresh();
    }

    public void inscrireCours() throws IOException, ClassNotFoundException {
        String prenomText = prenomField.getText();
        String nomText = nomField.getText();
        String emailText = emailField.getText();
        String matriculeText = matriculeField.getText();

        Course selectedCourse = tableCours.getSelectionModel().getSelectedItem();

        String output = client.register(prenomText,nomText,emailText,matriculeText,selectedCourse);

        if(output.contains("Félicitations!")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message");
            alert.setHeaderText("Message");
            alert.setContentText(output);
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText(output);
            alert.showAndWait();
        }
    }

}
