package client;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * La classe Client est une implémentation d'un client qui se connecte à un serveur et envoie des demandes.
 */
public class Client {


    public final static String REGISTER_COMMAND = "INSCRIRE";

    /**
     * String utilisé pour la commande Charger.
     */
    public final static String LOAD_COMMAND = "CHARGER ";

    private final String hostName;
    private final int port;
    private Socket clientSocket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    /**
     * Crée une nouvelle instance de client qui se connecte au serveur spécifié.
     * @param hostName l'addresse ip ou nom d'hôte du serveur
     * @param port le numéro de port du serveur
     * @throws IOException si une erreur d'I/O survient lors de la création du socket client
     */
    public Client(String hostName, int port) throws IOException {
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * charge les cours disponibles selon la session en paramêtre et les retourne.
     * @param currentSession la session courante
     * @return ArrayList<Course> la liste des cours disponibles
     */
    public ArrayList<Course> loadCourses(String currentSession) {
        ArrayList<Course> courses = null;
        try {
            openSocket();
            String msg = LOAD_COMMAND + currentSession;
            objectOutputStream.writeObject(msg);
            courses = (ArrayList<Course>) objectInputStream.readObject();
            closeSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Valide les paramêtres recus pour l'inscription, Envoie un formulaire d'inscription au serveur si valide  et retourne la réponse du serveur.
     * @param firstName le prénom de l'étudiant
     * @param lastName l'email de l'étudiant
     * @param email l'email de l'étudiant
     * @param matricule le matricule de l'étudiant
     * @param course le cours choisi
     * @return String la réponse du serveur
     */
    public String register(String firstName, String lastName, String email, String matricule, Course course) {
        ArrayList<String> errors = new ArrayList<>();


        if (firstName.equals("")) {
            errors.add("Le prénom  est invalide.");
        }
        if (lastName.equals("")) {
            errors.add("Le nom est invalide.");
        }
        if (!(email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))) {
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

    /**
     * Ouvre une connexion avec le serveur.
     * @throws IOException si une erreur survient lors de l'ouverture de la connexion
     */
    public void openSocket() throws IOException {

        clientSocket = new Socket(hostName, port);
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     * Ferme la connexion avec le serveur.
     * @throws IOException si une erreur survient lors de la fermeture de la connexion
     */
    private void closeSocket() throws IOException {
        objectInputStream.close();
        objectOutputStream.close();
        clientSocket.close();
    }
}
