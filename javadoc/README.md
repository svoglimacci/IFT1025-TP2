# Documentation

* **Author:**
    * Simon Voglimacci Stephanopoli 20002825
    * Victor Leblond
* **Version:** 1.0
* **Since:** 2023-03-30

## `public class Server`

La classe Serveur est une implémentation d'un serveur qui écoute les connexions
entrantes des clients et traite leurs demandes.

## `public Server(int port) throws IOException`

Crée une nouvelle instance de serveur qui écoute les connexions entrantes sur
le port spécifié.

* **Parameters:** `port` — le numéro de port sur lequel écouter
* **Exceptions:** `IOException` — si une erreur d'I/O survient lors de la création de la socket du serveur

## `public void addEventHandler(EventHandler h)`

Ajoute un nouveau gestionnaire d'événements à la liste des gestionnaires d'événements de ce serveur.

* **Parameters:** `h` — le gestionnaire d'événement à ajouter

## `private void alertHandlers(String cmd, String arg)`

Alerte tous les gestionnaires d'événements de la liste avec la commande et l'argument donnés.

* **Parameters:**
    * `cmd` — la commande à envoyer aux gestionnaires d'événements
    * `arg` — l'argument à envoyer aux gestionnaires d'événements

## `public void run()`

La function 'run' Écoute et accepte les connexions entrantes des clients et configure
les flux d'entrée et de sortie. Utilise les méthodes `listen()` et `disconnect()`pour
écouter les demandes du client et fermer la connexion.

## `public void listen() throws IOException, ClassNotFoundException`

Écoute les demandes entrantes du client et les traite en appelant les gestionnaires d'événements appropriés.

* **Exceptions:**
    * `IOException` — si une erreur d'I/O se produit lors de la lecture du flux d'entrée
    * `ClassNotFoundException` — si la classe de l'objet reçu du flux d'entrée est introuvable.

## `public Pair<String, String> processCommandLine(String line)`

La méthode 'processCommandLine' sépare la commande et l'argument d'une ligne de commande.

* **Parameters:** `line` — la ligne de commande à séparer.
* **Returns:** un objet 'Pair' contenant la commande et l'argument.

## `public void disconnect() throws IOException`

La méthode 'disconnect' ferme les flux d'entrée et de sortie et ferme la connexion avec le client.

* **Exceptions:** `IOException` — si une erreur I/O se produit lors de la fermeture du flux ou du socket.

## `public void handleEvents(String cmd, String arg)`

Traite les événements reçus du client en fonction de la commande et de l'argument reçus.

* **Parameters:**
    * `cmd` — la commande reçue du client
    * `arg` — l'argument reçu du client, le cas échéant

## `public void handleLoadCourses(String arg)`

Lire un fichier texte contenant des informations sur les cours et les transformer en liste d'objets `Course`.
La méthode filtre les cours par la session spécifiée en argument.
Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet `objectOutputStream`.
La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans
le flux.

* **Parameters:** `arg` — la session pour laquelle on veut récupérer la liste des cours

## `public void handleRegistration()`

Récupérer l'objet `RegistrationForm` envoyé par le client en utilisant `objectInputStream`, l'enregistrer
dans un fichier texte et renvoyer un message de confirmation au client.
La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un
fichier ou dans le flux de sortie.