package client;

import client.models.Course;
import client.models.RegistrationForm;

import java.util.ArrayList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class Client {

    public static Socket createSocket(String hostName, int port) throws IOException {
        return new Socket(hostName, port);

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {


            Scanner sc = new Scanner(System.in);
            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");

            int menuChoice = 1;
            while (true) {
                switch (menuChoice) {
                    case 1 -> {
                                        Socket socket = createSocket("localhost", 1337);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                        coursesMenu(sc, objectOutputStream, objectInputStream);

                    }
                    case 2 -> {
                                        Socket socket = createSocket("localhost", 1337);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                        registerMenu(sc, objectOutputStream, objectInputStream);

                    }
                    case 3 -> {

                        System.out.println("Au revoir!");
                        return;
                    }
                    default -> System.out.println("Choix invalide");
                }

                mainMenu();

                menuChoice = sc.nextInt();

            }
        }
    public static void mainMenu() {
        System.out.println("\n1. Consulter les cours offerts pour une autre session");
        System.out.println("2. Inscription à un cours");
        System.out.println("3. Quitter");
        System.out.print("> Choix: ");
    }

    public static void coursesMenu(Scanner sc, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {

        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
        System.out.println("1. Automne");
        System.out.println("2. Hiver");
        System.out.println("3. Été");
        System.out.print("> Choix: ");
        String choice = "";

        switch (sc.nextInt()) {
            case 1 -> choice = "Automne";
            case 2 -> choice = "Hiver";
            case 3 -> choice = "Été";
            default -> System.out.println("Choix invalide");
        }
        objectOutputStream.writeObject("CHARGER " + choice );

        //TODO: Cast Course and print

    }

    public static void registerMenu(Scanner sc, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {

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

        // create new Course from code
        Course course = new Course("test", "test","test");

        RegistrationForm form = new RegistrationForm(firstName, lastName, email, matricule, code);
        objectOutputStream.writeObject(form);

        String error = (String) objectInputStream.readObject();
        if (error.equals("Course not found")) {
            System.out.println("Le cours " + code + " n'existe pas.");
            return;
        }
        System.out.println("Félicitations! Inscription réussie de " + firstName + " au cours " + code + ".");

    }





}


