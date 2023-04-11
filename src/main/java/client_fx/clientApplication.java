package client_fx;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class clientApplication extends Application {

    public static Socket createSocket(String hostName, int port) throws IOException {
        return new Socket(hostName, port);
    }
    private TableView<Course> table = new TableView<Course>();

    public static void main(String[] args) throws IOException {

        launch();
    }
    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {

        final VBox leftSection = new VBox();
        final VBox rightSection = new VBox();
        leftSection.setSpacing(5);
        leftSection.setPadding(new Insets(10, 0, 0, 10));
        rightSection.setSpacing(5);
        rightSection.setPadding(new Insets(10, 0, 0, 10));
        leftSection.setAlignment(Pos.CENTER);

        Scanner sc = new Scanner(System.in);


        Scene scene = new Scene(new Group());
        stage.setTitle("Table View Sample");
        stage.setWidth(600);
        stage.setHeight(500);

        final Label label = new Label("Liste des cours");
        label.setFont(new Font("Arial", 20));

        table.setEditable(true);

        TableColumn<Course, String> courseNameCol = new TableColumn<>("Code");
        courseNameCol.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));

        TableColumn courseCodeCol = new TableColumn("Cours");
        courseCodeCol.setCellValueFactory(new PropertyValueFactory<Course, String>("code"));

        table.getColumns().addAll(courseNameCol, courseCodeCol);

        table.setFixedCellSize(25);
        table.prefHeightProperty().bind(stage.heightProperty().subtract(150));

        leftSection.getChildren().addAll(label, table);

        HBox sessionSelection = new HBox(20);

        ComboBox comboBox = new ComboBox();

        comboBox.getItems().add("Automne");
        comboBox.getItems().add("Hiver");
        comboBox.getItems().add("Ete");
        comboBox.getSelectionModel().selectFirst();

        sessionSelection.getChildren().add((comboBox));

        Button button = new Button("Charger");

        button.setOnAction(event -> {

            Socket socket = null;
            ObjectOutputStream objectOutputStream = null;
            ObjectInputStream objectInputStream = null;
            try {
                socket = createSocket("localhost", 1337);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ArrayList<Course> courses = new ArrayList<Course>();

            String value = (String) comboBox.getValue();

            try {
                courses = chargerNouvelleSession(value, objectOutputStream,objectInputStream);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            table.getItems().clear();

            for (Course course : courses) {
                table.getItems().add(course);
            }
        });

        Label titreFormulaire = new Label("Formulaire d'inscription");

        titreFormulaire.setFont(new Font("Arial", 24));

        Label prenomLabel = new Label("Prenom");
        TextField prenomInput = new TextField ();
        HBox prenomBox = new HBox();
        prenomBox.getChildren().addAll(prenomLabel, prenomInput);
        prenomBox.setSpacing(10);

        Label nomLabel = new Label("Nom");
        TextField nomInput = new TextField ();
        HBox nomBox = new HBox();
        nomBox.getChildren().addAll(nomLabel, nomInput);
        nomBox.setSpacing(10);

        final HBox finalRender = new HBox();

        leftSection.getChildren().addAll(button, sessionSelection);
        rightSection.getChildren().addAll(titreFormulaire, prenomBox, nomBox);

        Separator separator2 = new Separator();
        separator2.setOrientation(Orientation.VERTICAL);

        separator2.setPadding(new Insets(10, 0, 0, 10));

        finalRender.getChildren().addAll(leftSection,separator2,rightSection);

        ((Group) scene.getRoot()).getChildren().addAll(finalRender);

        stage.setScene(scene);

        stage.show();
    }

    public ArrayList<Course> chargerNouvelleSession(String session, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException{
        objectOutputStream.writeObject("CHARGER " + session);
        ArrayList<Course> courses = (ArrayList<Course>) objectInputStream.readObject();
        return courses;
    }

}
