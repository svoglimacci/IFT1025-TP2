package client.client_simple;

import java.io.IOException;

public class ClientLauncher {
    private final static int PORT = 1337;
    private final static String HOST_NAME = "127.0.0.1";


    public static void main(String[] args) {
        CLI client;
        try {
            client = new CLI(HOST_NAME, PORT);
            client.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}