package ch.tofind.commusica.network;

import java.net.NetworkInterface;

class Test {
    public static void main(String... args) {
        Server server = new Server(8080);
        server.serveClients();

        for(int i = 0; i < 6; ++i) {
            Client client = new Client();

            client.connect("localhost", 8080);
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }/*
        client.disconnectClientOnly();

        NetworkUtils.wait(3);
        client.fullDisconnect();

        NetworkUtils.wait(4);
        client.connect("localhost", 8080);*/
    }
}
