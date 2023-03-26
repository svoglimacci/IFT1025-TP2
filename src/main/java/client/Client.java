package client;

import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import server.models.Course;
import server.models.RegistrationForm;

public class Client {

    public static void main(String[] args) {
        try (

                Socket socket = new Socket("localhost", 1337);
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            )
        {
            Scanner sc = new Scanner(System.in);
            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");

            int menuChoice = 1;
            while (true) {
                switch (menuChoice) {
                    case 1 -> {
                        coursesMenu(sc, objectOutputStream, objectInputStream);


                    }
                    case 2 -> {
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
        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
        String session = "";

        switch (sc.nextInt()) {
            case 1 -> session = "Automne";
            case 2 -> session = "Hiver";
            case 3 -> session = "Été";
            default -> System.out.println("Choix invalide");
        }
        objectOutputStream.writeObject("CHARGER " + session );
        ArrayList<Course> courses = (ArrayList<Course>) objectInputStream.readObject();
        System.out.println("Les Cours offerts pendant la session " + session + " sont:");
        for (Course course : courses) {
            if (course.getSession().equals(session)) {
                System.out.println(course.getCode() + "\t" + course.getName());
            }
        }
    }

    public static RegistrationForm registerMenu(Scanner sc, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {

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

        RegistrationForm form = new RegistrationForm(firstName, lastName, email, matricule, course);
        objectOutputStream.writeObject(form);
        System.out.println("Félicitations! Inscription réussie de " + firstName + " au cours " + code + ".");

        return form;
    }





}


