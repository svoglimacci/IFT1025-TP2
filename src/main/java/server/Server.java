package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Simon Voglimacci Stephanopoli    20002825
 * @author Victor Leblond
 * @version 1.0
 * @since 2023-03-30
 */

/**
 * La classe Serveur est une implémentation d'un serveur qui écoute les connexions entrantes des clients et traite leurs demandes.
 */
public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Crée une nouvelle instance de serveur qui écoute les connexions entrantes sur le port spécifié.
     *
     * @param port le numéro de port sur lequel écouter
     * @throws IOException si une erreur d'I/O survient lors de la création de la socket du serveur
     */

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Ajoute un nouveau gestionnaire d'événements à la liste des gestionnaires d'événements de ce serveur.
     *
     * @param h le gestionnaire d'événement à ajouter
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * Alerte tous les gestionnaires d'événements de la liste avec la commande et l'argument donnés.
     *
     * @param cmd la commande à envoyer aux gestionnaires d'événements
     * @param arg l'argument à envoyer aux gestionnaires d'événements
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * La function 'run' Écoute et accepte les connexions entrantes des clients et configure les flux d'entrée et de sortie.
     * Utilise les méthodes {@link #listen()} et {@link #disconnect()} pour écouter les demandes du client et fermer la connexion.
     */

    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Écoute les demandes entrantes du client et les traite en appelant les gestionnaires d'événements appropriés.
     *
     * @throws IOException            si une erreur d'I/O se produit lors de la lecture du flux d'entrée
     * @throws ClassNotFoundException si la classe de l'objet reçu du flux d'entrée est introuvable.
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * La méthode 'processCommandLine' sépare la commande et l'argument d'une ligne de commande.
     *
     * @param line la ligne de commande à séparer.
     * @return un objet 'Pair' contenant la commande et l'argument.
     */

    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * La méthode 'disconnect' ferme les flux d'entrée et de sortie et ferme la connexion avec le client.
     *
     * @throws IOException si une erreur I/O se produit lors de la fermeture du flux ou du socket.
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Traite les événements reçus du client en fonction de la commande et de l'argument reçus.
     *
     * @param cmd la commande reçue du client
     * @param arg l'argument reçu du client, le cas échéant
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     * Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     * La méthode filtre les cours par la session spécifiée en argument.
     * Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     * La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     *
     * @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        ArrayList<Course> courses = new ArrayList<>();
        ArrayList<Course> filteredCourses = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader("src/main/java/server/data/cours.txt"));
            String str;

            while ((str = in.readLine()) != null) {
                String[] parts = str.split("\\s+");
                String code = parts[0];
                String name = parts[1];
                String session = parts[2];
                Course course = new Course(code, name, session);
                courses.add(course);
            }

            for (Course course : courses) {
                if (course.getSession().equals(arg)) {
                    filteredCourses.add(course);
                }
            }

            objectOutputStream.writeObject(filteredCourses);
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     * et renvoyer un message de confirmation au client.
     * La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        try {
            RegistrationForm registrationForm = (RegistrationForm) objectInputStream.readObject();
            FileWriter fileWriter = new FileWriter("src/main/java/server/data/inscription.txt", true);
            Course course = registrationForm.getCourse();
            BufferedReader in = new BufferedReader(new FileReader("src/main/java/server/data/cours.txt"));
            String str;
            String result = "Course not found";
            while ((str = in.readLine()) != null) {
                if (str.startsWith(course.getCode())) {
                    String session = str.split("\\s+")[2];
                    result = "Course found";
                    course.setName(str.split("\\s+")[1]);
                    course.setSession(session);

                    String formToFile = session + "\t" + course.getCode() + "\t" + registrationForm.getMatricule() + "\t" + registrationForm.getPrenom() + "\t" + registrationForm.getNom() + "\t" + registrationForm.getEmail();
                    fileWriter.write(formToFile + "\n");
                    System.out.println("Course " + formToFile);
                }
            }

            System.out.println(result);
            objectOutputStream.writeObject(result);
            in.close();
            fileWriter.flush();
            fileWriter.close();


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}

