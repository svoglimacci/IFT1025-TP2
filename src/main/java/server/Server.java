package server;

import javafx.util.Pair;
import server.models.Course;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

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

    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

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
            String registrationForm = (String) objectInputStream.readObject();
            FileWriter fileWriter = new FileWriter("src/main/java/server/data/inscription.txt", true);

            String[] parts = registrationForm.split("\\s+");
            String firstName = parts[0];
            String lastName = parts[1];
            String email = parts[2];
            String matricule = parts[3];
            String code = parts[4];

            BufferedReader in = new BufferedReader(new FileReader("src/main/java/server/data/cours.txt"));
            String str;
            String result = "Course not found";
            while ((str = in.readLine()) != null) {
                if (str.startsWith(code)) {
                    String session = str.split("\\s+")[2];
                    result = "Course found";
                    String formToFile = session + "\t" + code + "\t" + matricule + "\t" + firstName + "\t" + lastName + "\t" + email;
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

