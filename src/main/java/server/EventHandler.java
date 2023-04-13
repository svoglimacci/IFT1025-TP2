package server;

/**
 * Cette interface permet de définir un gestionnaire d'événements.
 * Elle définit les actions à effectuer lorsqu'un événement est déclenché grace à la méthode handle.
 */
@FunctionalInterface

public interface EventHandler {
    /**
     * Cette méthode est appelée lorsqu'un événement est déclenché.
     * @param cmd la commande à envoyer aux gestionnaires d'événements
     * @param arg l'argument à envoyer aux gestionnaires d'événements
     */
    void handle(String cmd, String arg);
}
