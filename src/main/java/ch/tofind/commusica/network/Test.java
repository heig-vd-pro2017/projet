package ch.tofind.commusica.network;

import java.net.NetworkInterface;

class Test {
    public static void main(String... args) {
        Server server = new Server(8080);
        server.serveClients();

        Client client = new Client();
        Client client2 = new Client();

        client.connect("localhost", 8080);
        client.connect("localhost", 8080);

        //client2.connect("localhost", 8080);



        //new Thread(client).start();

    }
}
