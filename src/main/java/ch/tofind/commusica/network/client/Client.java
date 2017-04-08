package ch.tofind.commusica.network.client;

import ch.tofind.commusica.network.NetworkUtils;
import ch.tofind.commusica.network.Protocol;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by David on 21.03.2017.
 */
public class Client {

    final static Logger LOG = Logger.getLogger(Client.class.getName());

    // id used to distinguish clients
    static int globalId = 0;
    private int id = ++globalId;
    private Socket serverSocket;
    private boolean isBinded = false;

    private int port;
    private InetAddress serverIP;

    private ClientDiscovery clientDiscovery;

    private MulticastReceiver multicastReceiver = null;

    private ArrayList<InetAddress> serversList = new ArrayList<>();

    private InetAddress addressOfInterface;

    BufferedReader in;
    PrintWriter out;

    public Client(InetAddress addressOfInterface) {
        clientDiscovery = new ClientDiscovery(addressOfInterface);
        this.addressOfInterface = addressOfInterface;
        multicastReceiver = new MulticastReceiver(addressOfInterface);

    }


    /**
     * Connect to a server of name 'serverName' and on port 'port'. It also send an id (the hash of the MAC address)
     * to the server to allow the latter creating a session for the client
     *
     * @param serverIP
     * @param port
     */
    public void connect(InetAddress serverIP, int port) {
        try {
            serverSocket = new Socket(serverIP, port);

            this.serverIP = serverIP;
            this.port = port;

            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));

            out.write(Protocol.CONNECTION_REQUEST + "\n");
            out.flush();

            String input;
            while ((input = in.readLine()) != null) {
                if (input.equals(Protocol.SEND_ID)) {
                    break;
                }
            }

            // we need to send MAC address here but for now we send id so we can test multiple clients on the same
            // computer
            //out.write(Integer.toString(id));
            out.write(Integer.toString(NetworkUtils.hashMACAddress()) + "\n");
            out.flush();



            while ((input = in.readLine()) != null) {
                if (input.equals(Protocol.SESSION_CREATED) || input.equals(Protocol.SESSION_UPDATED)) {
                    break;
                }
            }

            if (!isBinded) {
                new Thread(multicastReceiver).start();
            }

            isBinded = true;

            System.out.println("Client " + id + " connected");

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
    public void disconnectTCPSocket() {
        try {
            serverSocket.close();
            in.close();
            out.close();
            System.out.println("Client " + id + ": main socket disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendInfo(String info) {
        if (isBinded) {
            connect(serverIP, port);
            out.write(Protocol.SEND_INFO + "\n");
            out.flush();
            out.write(info + "\n");
            out.flush();

            disconnectTCPSocket();
        } else {
            LOG.info("No server set, connot send info");
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

            multicastReceiver.stop();
            isBinded = false;
            System.out.println("Client " + id + " is fully disconnected");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void refreshServers() {
        new Thread(clientDiscovery).start();
    }
}
