package client;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Client {


    public static Socket createSocket(String hostName, int port) throws IOException {
        return new Socket(hostName, port);

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String currentSession = "";
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");

        int menuChoice = 1;
        while (true) {
            try {
                switch (menuChoice) {
                    case 1 -> {
                        Socket socket = createSocket("localhost", 1337);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                        currentSession = coursesMenu(sc, objectOutputStream, objectInputStream);

                    }
                    case 2 -> {
                        Socket socket = createSocket("localhost", 1337);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                        registerMenu(sc, currentSession, objectOutputStream, objectInputStream);

                    }
                    case 3 -> {

                        System.out.println("Au revoir!");
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Choix invalide");
                }

                mainMenu();
                menuChoice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Choix invalide");
                sc.next();

            }
        }

    }

    public static void mainMenu() {
        System.out.println("\n1. Consulter les cours offerts pour une autre session");
        System.out.println("2. Inscription à un cours");
        System.out.println("3. Quitter");
        System.out.print("> Choix: ");
    }

    public static String coursesMenu(Scanner sc, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        ArrayList<Course> courses;

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

        objectOutputStream.writeObject("CHARGER " + choice);

        courses = (ArrayList<Course>) objectInputStream.readObject();

        for (Course course : courses) {
            System.out.println(course.getCode() + "\t" + course.getName());
        }
        objectOutputStream.close();
        objectInputStream.close();
        return choice;

    }

    public static void registerMenu(Scanner sc, String currentSession, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {

        ArrayList<String> errors = new ArrayList<>();
        boolean validation = true;
        while (validation) {
            objectOutputStream.writeObject("INSCRIRE ");
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

            RegistrationForm registrationForm = new RegistrationForm(firstName, lastName, email, matricule, new Course("", code, currentSession));

            if (registrationForm.getPrenom().equals("")) {
                errors.add("Le prénom  est invalide.");
            }
            if (registrationForm.getNom().equals("")) {
                errors.add("Le nom est invalide.");
            }
            if (!(registrationForm.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$"))) {
                errors.add("L'email est invalide.");
            }
            if (!(registrationForm.getMatricule().matches("^([0-9]{8})"))) {
                errors.add("Le matricule  est invalide");
            }
            if (!(registrationForm.getCourse().getCode().matches("^[A-Z]{3}[0-9]{4}"))) {
                System.out.print(registrationForm.getEmail());
                errors.add("Le code du cours est invalide");
            }
            if (errors.size() == 0) {
                validation = false;
                objectOutputStream.writeObject(registrationForm);
                String error = (String) objectInputStream.readObject();

                if (error.equals("Course not found")) {
                    System.out.println("Le cours " + code + " n'existe pas.");
                    objectOutputStream.close();
                    objectInputStream.close();
                    return;
                }
                System.out.println("Félicitations! Inscription réussie de " + firstName + " au cours " + code + ".");
                objectOutputStream.close();
                objectInputStream.close();
            } else {
                System.out.println("Les erreurs suivantes ont été détectées:");
                for (String error : errors) {
                    System.out.println(error);
                    objectOutputStream.close();
                    objectInputStream.close();
                }
                errors.clear();
            }


        }


    }
}
