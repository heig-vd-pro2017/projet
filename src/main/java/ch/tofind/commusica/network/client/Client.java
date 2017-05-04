package ch.tofind.commusica.network.client;

import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.utils.Network;
import ch.tofind.commusica.network.Protocol;
import ch.tofind.commusica.playlist.PlaylistUpdateReceiver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @brief This class represents a network client.
 */
public class Client {

    //! Logger for debugging proposes.
    final static Logger LOG = Logger.getLogger(Client.class.getName());

    //!
    static int globalId = 0;

    //!
    private int id = ++globalId;

    //!
    private Socket serverSocket;

    //!
    private boolean isBinded = false;

    //!
    private int port;

    //!
    private InetAddress serverIP;

    //!
    private ServerDiscovery serverDiscovery;

    //!
    private PlaylistUpdateReceiver playlistUpdateReceiver = null;

    //!
    private ArrayList<InetAddress> serversList = new ArrayList<>();

    //!
    private InetAddress addressOfInterface;

    //!
    BufferedReader in;

    //!
    PrintWriter out;

    /**
     * @brief
     */
    public Client() {
        serverDiscovery = new ServerDiscovery();
        this.addressOfInterface = Network.getAddressOfInterface();
        playlistUpdateReceiver = new PlaylistUpdateReceiver();
    }

    /**
     * @brief Connect to a server of name 'serverName' and on port 'port'. It also send an id (the hash of the MAC address)
     * to the server to allow the latter creating a session for the client.
     * It is used only once to connect to the server and set the port and IP if the server.
     * If you want to reconnect once you used this method, use the connect() method (which just call this method
     * but with the port and IP already saved in the Client
     *
     * @param serverIP
     * @param port
     * @param request
     */
    public void connect(InetAddress serverIP, int port, String request) {
        try {
            // Create the socket and the IOs
            serverSocket = new Socket(serverIP, port);
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));

            this.serverIP = serverIP;
            this.port = port;

            // send the CONNECTION_REQUEST
            out.write(request + "\n");
            out.flush();

            // Wait for the SEND_ID request
            String input;
            while ((input = in.readLine()) != null) {
                if (input.equals(Protocol.SEND_ID)) {
                    break;
                }
            }

            // Send the hash of our MAC address
            //out.write(Integer.toString(id));
            out.write(Integer.toString(Network.hashMACAddress()) + "\n");
            out.flush();

            // wait for the acknowledge that the session is created or updated
            while ((input = in.readLine()) != null) {
                if (input.equals(Protocol.SESSION_STORED)) {
                    break;
                }
            }
            // create the playlistUpdate receiver thread and start it
            if (!isBinded) {
                new Thread(playlistUpdateReceiver).start();
                // We connected to the server once
                isBinded = true;
            }

            System.out.println("Client " + id + " connected and binded.");


        } catch (IOException e) {
            // we check if some resources should be closed
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }

            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * @brief Used to reconnect to the server once you called the connect(InetAddress serverIP, int port, String request)
     * method. It sends the RECONNECTION_REQUEST request to allow the server to check for a command after the
     * authentication phase.
     */
    public void reconnect() {
        connect(serverIP, port, Protocol.RECONNECTION_REQUEST);
    }

    /**
     * @brief Retrive a list of IPs of available servers.
     *
     * @return the list of IPs of available servers.
     */
    public ArrayList<InetAddress> getServersList() {
        return serverDiscovery.getServersList();
    }

    /**
     * @brief Disconnect only the client, thus letting the multicast receive information.
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

    /**
     * @brief Send a String to the server
     * It must be called only once you already connected to the server and set the server port and IP
     *
     * @param msg
     */
    public void sendString(String msg) {
        reconnect();
        out.write(Protocol.SEND_INFO + "\n");
        out.flush();
        out.write(msg + "\n");
        out.flush();
        disconnectTCPSocket();
    }

    /**
     * @brief Disconnect the client and it's playlist receiver thread
     */
    public void fullDisconnect() {
        disconnectTCPSocket();
        playlistUpdateReceiver.stop();
        isBinded = false;
        System.out.println("Client " + id + " is fully disconnected");
    }

    /**
     * refresh the servers list
     */
    public void refreshServers() {
        new Thread(serverDiscovery).start();
    }

    /**
     * Send a song to the server.
     * It must be called only once you already connected to the server and set the server port and IP
     *
     * @param path
     */
    public void sendSong(String path) {
        try {
            reconnect();

            out.write(Protocol.SEND_MUSIC + "\n");
            out.flush();


            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            BufferedOutputStream bos = new BufferedOutputStream(serverSocket.getOutputStream());

            byte[] contents = new byte[8192];
            int size;

            bis.read(contents, 0, 16);

            if (FileManager.signatureChecker(contents)) {
                System.out.println("File OK");
                bos.write(contents, 0, 16);
                bos.flush();
                while ((size = bis.read(contents)) != -1) {
                    bos.write(contents, 0, size);
                }
                bos.flush();
            } else {
                System.out.println("File NOT OK!");
            }

            fis.close();
            bis.close();
            bos.close();

            LOG.info("Music sent!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        disconnectTCPSocket();

    }
}