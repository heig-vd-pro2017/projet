package ch.tofind.commusica.network;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by David on 21.03.2017.
 */
public class Client implements NetPort {

    // id used to distinguish clients
    static int globalId = 0;
    private int id = ++globalId;
    private Socket serverSocket;
    // see if we can do better than a boolean
    private boolean isConnected = false;

    private int port;

    private ClientDiscovery clientDiscovery;

    private ArrayList<InetAddress> serversList = new ArrayList<>();

    BufferedReader in;
    PrintWriter out;

    public Client() {
        clientDiscovery = new ClientDiscovery();
    }


    /**
     * Connect to a server of name 'serverName' and on port 'port'. It also send an id (the hash of the MAC address)
     * to the server to allow the latter creating a session for the client
     * @param serverIP
     * @param port
     */
    public void connect(InetAddress serverIP, int port) {
        try {
            if (!isConnected) {
                serverSocket = new Socket(serverIP, port);
                this.port = port;

                in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));

                String input;
                while ((input = receive()) != null) {
                    if (input.equals(SEND_ID)) {
                        break;
                    }
                }
                // we need to send MAC address here but for now we send id so we can test multiple clients on the same
                // computer
                send(Integer.toString(id));
                //send(Long.toString(NetworkUtils.hashMACAddress()));

                while ((input = receive()) != null) {
                    if (input.equals(SESSION_CREATED) || input.equals(SESSION_UPDATED)) {
                        break;
                    }
                }
                isConnected = true;

                System.out.println("ClientDiscovery " + id + " connected");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<InetAddress> getServersList() {
        return clientDiscovery.getServersList();
    }

    /**
     * Disconnect only the client, thus letting the multicast receive informations
     */
    public void disconnectClientOnly() {
        try {
            serverSocket.close();
            in.close();
            out.close();
            isConnected = false;
            System.out.println("ClientDiscovery " + id + " is disconnected (client only)");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Disconnect the client and it's multicast client
     */
    public void fullDisconnect() {
        try {
            serverSocket.close();
            in.close();
            out.close();
            isConnected = false;
            System.out.println("ClientDiscovery " + id + " is fully disconnected");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String str) {
        out.write(str);
        out.write('\n');
        out.flush();
    }

    public String receive() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void refreshServers() {
        new Thread(clientDiscovery).start();
    }
}
