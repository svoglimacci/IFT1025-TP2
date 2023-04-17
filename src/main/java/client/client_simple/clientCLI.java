package client.client_simple;

import client.Client;
import server.models.Course;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

/**
 * La classe clientCLI est une implémentation d'un client de ligne de commande qui sert d'interface à la classe Client.
 */
public class clientCLI {

    private final Client client;
    /**
     * String utilisé pour la commande Inscrire.
     */
    private final static int PORT = 1337;

    /**
     * String utilisé pour le nom d'hôte.
     */
    private final static String HOST_NAME = "127.0.0.1";

    /**
     * Crée une nouvelle instance de clientCLI qui se connecte au serveur spécifié.
     * @param hostName l'addresse ip ou nom d'hôte du serveur
     * @param port le numéro de port du serveur
     * @throws IOException si une erreur d'I/O survient lors de la création du socket client
     */
    public clientCLI(String hostName, int port) throws IOException {
        this.client = new Client(hostName, port);
    }

    /**
     * Démarre le client, affiche le menu et gère les choix de l'utilisateur par des appels aux méthodes de Client appropriées.
     * @throws IOException
     */
    public void run() throws IOException {
        String currentSession = "";
        ArrayList<Course> courses = null;
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
        int menuChoice = 1;
        while (true) {
            try {
                switch (menuChoice) {
                    case 1 -> {
                        currentSession = coursesMenu(sc);
                        if (!Objects.equals(currentSession, "")) {
                            System.out.println("Les cours offerts pendant la session d'" + currentSession.toLowerCase() + " sont:");
                            courses = client.loadCourses(currentSession);
                            for (Course course : courses) {
                                System.out.println(course.getCode() + "\t" + course.getName());
                            }
                        }
                    }
                    case 2 -> {
                        registerMenu(sc, courses);
                    }
                    case 3 -> {
                        System.out.println("Au revoir!");
                        sc.close();
                        return;
                    }
                    default -> {
                        System.out.println("Choix invalide");
                    }
                }
                //if currentSession is not empty, then we can display the main menu, else coursesMenu(sc)
                if (!currentSession.equals("")) {
                    mainMenu();
                    menuChoice = sc.nextInt();
                }

            } catch (InputMismatchException e) {
                System.out.println("Choix invalide");
                sc.next();
            }
        }
    }

    /**
     * Affiche le menu principal.
     */
    private void mainMenu() {
        System.out.println("\n1. Consulter les cours offerts pour une autre session");
        System.out.println("2. Inscription à un cours");
        System.out.println("3. Quitter");
        System.out.print("> Choix: ");
    }

    /**
     * Affiche le menu pour choisir la session et retourne la session choisie.
     * @param sc le scanner utilisé pour lire les entrées de l'utilisateur
     * @return la session choisie
     */
    private String coursesMenu(Scanner sc) {

        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
        System.out.println("1. Automne");
        System.out.println("2. Hiver");
        System.out.println("3. Été");
        System.out.print("> Choix: ");

        String choice = "";
        try {
            switch (sc.nextInt()) {
                case 1 -> choice = "Automne";
                case 2 -> choice = "Hiver";
                case 3 -> choice = "Ete";
                default -> System.out.println("Choix invalide");
            }
        } catch (InputMismatchException e) {
            System.out.println("Choix invalide");
            sc.next();
        }
        return choice;
    }

    /**
     * Affiche le menu pour inscrire un étudiant à un cours.
     * @param sc le scanner utilisé pour lire les entrées de l'utilisateur
     * @param courses la liste des cours offerts pendant la session courante
     */
    private void registerMenu(Scanner sc, ArrayList<Course> courses) {
        String response;
        System.out.print("Veuillez saisir votre prénom: ");
        String firstName = sc.next();
        System.out.print("Veuillez saisir votre nom: ");
        String lastName = sc.next();
        System.out.print("Veuillez saisir votre email: ");
        String email = sc.next();
        System.out.print("Veuillez saisir votre matricule: ");
        String matricule = sc.next();
        System.out.print("Veuillez saisir le code du cours: ");
        String code = sc.next();
        response = client.register(firstName, lastName, email, matricule, getCourse(code, courses));
        System.out.println(response);
    }

    /**
     * Retourne le cours dont le code est spécifié.
     * @param code le code du cours
     * @param courses la liste des cours offerts pendant la session courante
     * @return le cours dont le code est spécifié
     */

    private Course getCourse(String code, ArrayList<Course> courses) {
        for (Course course : courses) {
            if (course.getCode().equals(code)) {
                return course;
            }
        }
        return null;
    }

    /**
     * Point d'entrée du programme. Crée une nouvelle instance de clientCLI et l'exécute.
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        clientCLI client;
        try {
            client = new clientCLI(HOST_NAME, PORT);
            client.run();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}