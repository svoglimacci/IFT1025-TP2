package client;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER ";
    private final String hostName;
    private final int port;
    private Socket clientSocket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public Client(String hostName, int port) throws IOException {
        this.hostName = hostName;
        this.port = port;
    }

    public ArrayList<Course> loadCourses(String currentSession) {
        ArrayList<Course> courses = null;
        try {
            openSocket();
            String msg = "CHARGER " + currentSession;
            objectOutputStream.writeObject(msg);
            courses = (ArrayList<Course>) objectInputStream.readObject();
            closeSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }

    public String register(String firstName, String lastName, String email, String matricule, Course course) throws IOException, ClassNotFoundException {
        ArrayList<String> errors = new ArrayList<>();


        if (firstName.equals("")) {
            errors.add("Le prénom  est invalide.");
        }
        if (lastName.equals("")) {
            errors.add("Le nom est invalide.");
        }
        if (!(email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))) {
            errors.add("L'email est invalide.");
        }
        if (!(matricule.matches("^([0-9]{8})"))) {
            errors.add("Le matricule  est invalide");
        }
        if (course == null) {
            errors.add("Le code du cours est invalide");
        }
        String response = null;
        if (errors.size() == 0) {
            try {
                RegistrationForm registrationForm = new RegistrationForm(firstName, lastName, email, matricule, course);
                openSocket();
                objectOutputStream.writeObject(REGISTER_COMMAND);
                objectOutputStream.writeObject(registrationForm);
                response = (String) objectInputStream.readObject();
                closeSocket();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return errors.size() == 0 ? response : String.join("\n", errors);
    }

    public void openSocket() throws IOException {

        clientSocket = new Socket(hostName, port);
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    private void closeSocket() throws IOException {
        objectInputStream.close();
        objectOutputStream.close();
        clientSocket.close();
    }
}
