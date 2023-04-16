package client.client_fx;

import client.Client;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import server.models.Course;

import java.io.IOException;

public class clientController {
    @FXML private ComboBox boxSession;
    @FXML private Button chargerButton;

    @FXML private TableView<Course> tableCours;
    @FXML private TableColumn<Course, String> codeColone;
    @FXML private TableColumn<Course, String> nomColone;
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
        System.out.println(client.loadCourses(currentSession));


        tableCours.getItems().clear();
        tableCours.setItems(FXCollections.observableList(client.loadCourses(currentSession)));
        tableCours.refresh();
    }

}
