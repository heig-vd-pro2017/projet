package ch.tofind.commusica.network;


import ch.tofind.commusica.network.client.Client;
import ch.tofind.commusica.network.server.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

class Test {
    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);
        int type = 0;

        InetAddress addressOfInterface = null;


        try {

                addressOfInterface = NetworkUtils.networkInterfaceChoser();

        } catch (SocketException e) {
            e.printStackTrace();
        }


        while (type != 1 && type != 2) {
            System.out.println("[1] server");
            System.out.println("[2] client");

            System.out.println("Which one are you?");
            System.out.print("> ");

            type = scanner.nextInt();
        }

        if (type == 1) {
            Server server = new Server(8081, addressOfInterface);
            server.serveClients();

            int actionServer = 0;
            while (server != null) {
                System.out.println("What do you want to do?");
                System.out.println("[1] Send Playlist Update to client");
                System.out.print("> ");

                actionServer = scanner.nextInt();

                switch (actionServer) {
                    case 1:
                        server.sendPlaylistUpdate();
                }
            }
        } else {
            Client client = new Client(addressOfInterface);

            int actionClient = 0;
            while (client != null) {
                System.out.println("What do you want to do?");
                System.out.println("[1] refresh server list");
                System.out.println("[2] connect to server");
                System.out.println("[3] Display available servers");
                System.out.println("[4] Disconnect");
                System.out.print("> ");

                actionClient = scanner.nextInt();

                switch (actionClient) {
                    case 1:
                        client.refreshServers();
                        break;
                    case 2:
                        System.out.println("To which server do you want to connect to?");
                        System.out.println(client.getServersList().toString());
                        client.connect(client.getServersList().get(scanner.nextInt() - 1), 8081);
                        break;
                    case 3:
                        System.out.println("Available servers");
                        System.out.println(client.getServersList().toString());
                        break;
                    case 4:
                        client.fullDisconnect();
                        client = null;
                }
            }

        }
    }


}
