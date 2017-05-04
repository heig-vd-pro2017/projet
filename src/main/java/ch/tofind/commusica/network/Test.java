package ch.tofind.commusica.network;


import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.network.client.Client;
import ch.tofind.commusica.network.server.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Test class
 */
class Test {
    public static void main(String... args) {

        Scanner scanner = new Scanner(System.in);

        //File file = new File("C:\\Users\\David\\Documents\\YourFuckingMother_x_EHDE_-_Pocket_Monsters_VIP.mp3");
        //File file = new File("C:\\Users\\David\\Downloads\\03 Heater.m4a");
        File file = new File("C:\\Users\\David\\Documents\\Natel\\Music\\Tristam Discography_\\Tristam\\00 - Undercat (final).wav");
        FileManager.displayMetadatas(file);

        InetAddress addressOfInterface = null;

        System.out.println("Which interface do you want to use?");
        ArrayList<NetworkInterface> interfaces = NetworkUtils.networkInterfaceChooser();
        for (int i = 1; i < interfaces.size(); ++i) {
            System.out.println("[" + i + "] " + interfaces.get(i-1).getName() + ": " + NetworkUtils.getInet4AddressString(NetworkUtils.getInet4Address(interfaces.get(i-1))));
        }

        NetworkUtils.setAddressOfInterface(NetworkUtils.getInet4Address(interfaces.get(scanner.nextInt() - 1)));

        while (true) {
            int type = 0;
            while (type != 1 && type != 2) {
                System.out.println("[1] server");
                System.out.println("[2] client");

                System.out.println("Which one are you?");
                System.out.print("> ");

                type = scanner.nextInt();
            }



            if (type == 1) {
                Server server = new Server(8081);
                server.serveClients();

                int actionServer = 0;
                while (server != null) {
                    System.out.println("What do you want to do?");
                    System.out.println("[1] Disconnect");
                    System.out.print("> ");

                    actionServer = scanner.nextInt();

                    switch (actionServer) {
                        case 1:
                            server.disconnect();
                            server = null;
                    }
                }
            } else {
                Client client = new Client();

                int actionClient = 0;
                while (client != null) {
                    System.out.println("What do you want to do?");
                    System.out.println("[1] refresh server list");
                    System.out.println("[2] connect to server");
                    System.out.println("[3] Display available servers");
                    System.out.println("[4] Disconnect");
                    System.out.println("[5] Send music");

                    System.out.print("> ");

                    actionClient = scanner.nextInt();

                    switch (actionClient) {
                        case 1:
                            client.refreshServers();
                            break;
                        case 2:
                            System.out.println("To which server do you want to connect to?");
                            System.out.println(client.getServersList().toString());
                            client.connect(client.getServersList().get(scanner.nextInt() - 1), 8081, Protocol.CONNECTION_REQUEST);
                            break;
                        case 3:
                            System.out.println("Available servers");
                            System.out.println(client.getServersList().toString());
                            break;
                        case 4:
                            client.fullDisconnect();
                            client = null;
                            break;
                        case 5:
                            client.sendSong("C:\\Users\\David\\Documents\\YourFuckingMother_x_EHDE_-_Pocket_Monsters_VIP.mp3");
                            break;
                        default:
                            System.out.println("Action not supported ");

                    }
                }

            }
        }
    }
}
