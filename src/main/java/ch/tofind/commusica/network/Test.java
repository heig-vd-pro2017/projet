package ch.tofind.commusica.network;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

class Test {
    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);
        int type = 0;

        while(type != 1 && type != 2) {
            System.out.println("[1] server");
            System.out.println("[2] client");

            System.out.println("Which one are you?");
            System.out.print("> ");

            type = scanner.nextInt();
        }

        if(type == 1) {
            Server server = new Server(8081);
            server.serveClients();
        } else {
            Client client = new Client();

            int actionClient = 0;
            while (true) {
                System.out.println("What do you want to do?");
                System.out.println("[1] refresh server list");
                System.out.println("[2] connect to server");
                System.out.println("[3] Display available servers");
                System.out.print("> ");

                actionClient = scanner.nextInt();

                switch (actionClient) {
                    case 1:
                        client.refreshServers();
                        break;
                    case 2:
                        System.out.println("To which server do you want to connect?");
                        System.out.println(client.getServersList().toString());
                        client.connect(client.getServersList().get(scanner.nextInt()), 8081);
                        break;
                    case 3:
                        System.out.println("Available servers");
                        System.out.println(client.getServersList().toString());
                        break;
                }
                client.refreshServers();
            }

        }
    }




}
