package client.client_fx;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class clientController {
    @FXML private ComboBox boxSession;
    @FXML private Button chargerButton;

    //@FXML private Label
    @FXML
    public void initialize() {
        // bind the selected fruit label to the selected fruit in the combo box.
        //selectedFruit.textProperty().bind(boxSession.getSelectionModel().selectedItemProperty());

        // listen for changes to the fruit combo box selection and update the displayed fruit image accordingly.
        boxSession.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> selected, String oldFruit, String newFruit) {

            }
        });
    }

    public void chargerSession(){

    }

}
