package client.client_fx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import server.models.Course;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class clientApplication extends Application {

    private TableView<Course> table = new TableView<Course>();

    public static Socket createSocket(String hostName, int port) throws IOException {
        return new Socket(hostName, port);

    }

    public static void main(String[] args) throws IOException {

        launch();
    }

    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.setAlignment(Pos.CENTER);

        Scanner sc = new Scanner(System.in);

        Socket socket = createSocket("localhost", 1337);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

        ArrayList<Course> courses = new ArrayList<Course>();

        objectOutputStream.writeObject("CHARGER " + "Automne");

        courses = (ArrayList<Course>) objectInputStream.readObject();

        System.out.println((courses));

        Scene scene = new Scene(new Group());
        stage.setTitle("Table View Sample");
        stage.setWidth(300);
        stage.setHeight(500);

        final Label label = new Label("Liste des cours");
        label.setFont(new Font("Arial", 20));

        table.setEditable(true);

        TableColumn<Course, String> courseNameCol = new TableColumn<>("Code");
        courseNameCol.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));

        TableColumn courseCodeCol = new TableColumn("Cours");
        courseCodeCol.setCellValueFactory(new PropertyValueFactory<Course, String>("code"));

        table.getColumns().addAll(courseNameCol, courseCodeCol);

        for (Course course : courses) {
            table.getItems().add(course);
        }

        vbox.getChildren().addAll(label, table);

        final Label bottomText = new Label("this is bottom text :)");
        vbox.getChildren().add(new Button("Button"));

        vbox.getChildren().add((bottomText));

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);

        stage.show();
    }


}
