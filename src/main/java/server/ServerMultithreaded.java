package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * @author Simon Voglimacci Stephanopoli    20002825
 * @author Victor Leblond 20244841
 * @version 1.0
 * @since 2023-03-30
 */

/**
 * La classe Serveur est une implémentation d'un serveur qui écoute les connexions entrantes des clients et traite leurs demandes.
 */
class ServerMultithreaded {

    public static void main(String[] args)
    {
        ServerSocket server;
        try {
            server = new ServerSocket(1337);
            System.out.println("Server is running...");
            while (true) {
                Socket client = server.accept();
                ClientHandler clientSocket = new ClientHandler(client);
                new Thread(clientSocket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {

        /**
         * String utilisé pour la commande Inscrire.
         */
        public final static String REGISTER_COMMAND = "INSCRIRE";
        /**
         * String utilisé pour la commande Charger.
         */
        public final static String LOAD_COMMAND = "CHARGER";
        private final ArrayList<EventHandler> handlers;
        private ObjectInputStream objectInputStream;
        private ObjectOutputStream objectOutputStream;
        private final Socket clientSocket;

        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
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
         * Écoute les demandes entrantes du client et les traite en appelant les gestionnaires d'événements appropriés.
         *
         * @throws IOException            si une erreur d'I/O se produit lors de la lecture du flux d'entrée
         * @throws ClassNotFoundException si la classe de l'objet reçu du flux d'entrée est introuvable.
         */
        public void listen() throws IOException, ClassNotFoundException {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String line;
            if ((line = this.objectInputStream.readObject().toString()) != null) {
                Pair<String, String> parts = processCommandLine(line);
                String cmd = parts.getKey();
                String arg = parts.getValue();
                System.out.printf(
                        " Envoi du client: %s\n",
                        line + " géré par le " + Thread.currentThread().getName());
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
            clientSocket.close();
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

        public void run()
        {
            try {
                objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                listen();
                disconnect();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Traite les demandes de chargement de cours du client. Charge les cours du fichier 'cours.txt' et les envoie au client.
         * seuls les cours de la session spécifiée dans l'argument sont envoyés au client.
         *
         * @param arg la session pour laquelle charger les cours.
         */
        public void handleLoadCourses(String arg) {

            System.out.println("Courses loading");
            ArrayList<Course> courses = new ArrayList<>();
            ArrayList<Course> filteredCourses = new ArrayList<>();
            try {
                BufferedReader in = new BufferedReader(new FileReader("src/main/java/server/data/cours.txt"));
                String str;

                while ((str = in.readLine()) != null) {
                    String[] parts = str.split("\\s+");
                    String code = parts[1];
                    String name = parts[0];
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
         * Traite les demandes d'inscription reçues du client, envoie une réponse approprié au client et enregistre les informations de l'inscription dans un fichier.
         *
         */
        public void handleRegistration() {
            try {
                RegistrationForm registrationForm = (RegistrationForm) objectInputStream.readObject();
                FileWriter fileWriter = new FileWriter("src/main/java/server/data/inscription.txt", true);
                Course course = registrationForm.getCourse();
                BufferedReader in = new BufferedReader(new FileReader("src/main/java/server/data/cours.txt"));
                String str;
                String result = "";
                while ((str = in.readLine()) != null) {

                    if (str.startsWith(course.getCode())) {
                        String session = str.split("\\s+")[2];
                        result = "Félicitations! Inscription réussie de " + registrationForm.getPrenom() + " au cours " + course.getCode() + ".";
                        course.setName(str.split("\\s+")[1]);
                        course.setSession(session);

                        String formToFile = session + "\t" + course.getCode() + "\t" + registrationForm.getMatricule() + "\t" + registrationForm.getPrenom() + "\t" + registrationForm.getNom() + "\t" + registrationForm.getEmail();
                        fileWriter.write(formToFile + "\n");
                        System.out.println("Course " + formToFile);
                    }
                }

                objectOutputStream.writeObject(result);
                in.close();
                fileWriter.flush();
                fileWriter.close();


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        }

    }

}

