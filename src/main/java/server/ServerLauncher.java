package server;

/**
 * Lance le serveur sur un port spécifié.
 */
public class ServerLauncher {
    /**
     * Le port sur lequel le serveur écoute les connexions entrantes.
     */
    public final static int PORT = 1337;

    /**
     * Lance le serveur sur le port spécifié.
     *
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}