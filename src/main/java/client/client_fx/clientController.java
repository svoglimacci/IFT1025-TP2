package client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
public class clientController {
    @FXML private Label label;
    @FXML private Button button;

    @FXML
    protected void handleButtonPress() {
        button.setVisible(false);
        label.setVisible(true);
    }

}
