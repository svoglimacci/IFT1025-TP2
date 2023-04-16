package client.client_simple;

import client.Client;
import server.models.Course;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

public class clientCLI {

    private final Client client;
    private final static int PORT = 1337;
    private final static String HOST_NAME = "127.0.0.1";

    public clientCLI(String hostName, int port) throws IOException {
        this.client = new Client(hostName, port);
    }

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

            } catch (InputMismatchException | ClassNotFoundException e) {
                System.out.println("Choix invalide");
                sc.next();
            }
        }
    }

    private void mainMenu() {
        System.out.println("\n1. Consulter les cours offerts pour une autre session");
        System.out.println("2. Inscription à un cours");
        System.out.println("3. Quitter");
        System.out.print("> Choix: ");
    }

    private String coursesMenu(Scanner sc) throws ClassNotFoundException {

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

    private void registerMenu(Scanner sc, ArrayList<Course> courses) throws IOException, ClassNotFoundException {
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

    private Course getCourse(String code, ArrayList<Course> courses) {
        for (Course course : courses) {
            if (course.getCode().equals(code)) {
                return course;
            }
        }
        return null;
    }

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