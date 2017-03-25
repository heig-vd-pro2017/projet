package ch.tofind.commusica.network;

import java.io.*;
import java.net.Socket;

/**
 * Created by David on 21.03.2017.
 */
public class Client extends NetPort {

    // id used to distinguish clients
    static int globalId = 0;
    private int id = ++globalId;
    private Socket serverSocket;
    // see if we can do better than a boolean
    private boolean isConnected = false;
    private boolean multicastClientConnected = false;
    private MulticastSocketClient multicastSocketClient;

    public Client() {
        multicastSocketClient = new MulticastSocketClient(id);
        new Thread(multicastSocketClient).start();
        multicastClientConnected = true;
    }


    /**
     * Connect to a server of name 'serverName' and on port 'port'. It also send an id (the hash of the MAC address)
     * to the server to allow the latter creating a session for the client
     * @param serverName
     * @param port
     */
    public void connect(String serverName, int port) {
        try {
            if (!isConnected) {
                serverSocket = new Socket(serverName, port);
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

                System.out.println("Client " + id + " connected");
            }
            if(!multicastClientConnected) {
                multicastSocketClient = new MulticastSocketClient(id);
                new Thread(multicastSocketClient).start();
                multicastClientConnected = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            System.out.println("Client " + id + " is disconnected (client only)");
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
            multicastClientConnected = false;
            multicastSocketClient.stop();
            System.out.println("Client " + id + " is fully disconnected");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
