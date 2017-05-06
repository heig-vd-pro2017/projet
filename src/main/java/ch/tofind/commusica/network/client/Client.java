package ch.tofind.commusica.network.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * @brief This class represents a network client.
 */
public class Client {

    //! Logger for debugging proposes.
    final static Logger LOG = Logger.getLogger(Client.class.getName());

    //!
    private InetAddress hostname;

    //! Socket to use for the communication
    private Socket socket;

    //! Port to use for the communication
    private int port;

    //! Where to send the output
    private PrintWriter out;

    //! Where to read the input
    private BufferedReader in;

    /**
     * @brief
     */
    public Client(InetAddress hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * @brief Connect to a server of name 'serverName' and on port 'port'. It also send an id (the hash of the MAC address)
     * to the server to allow the latter creating a session for the client.
     * It is used only once to connect to the server and set the port and IP if the server.
     * If you want to reconnect once you used this method, use the connect() method (which just call this method
     * but with the port and IP already saved in the Client
     *
     */
    public void connect() {

        try {
            socket = new Socket(hostname, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void disconnect() {

        out.close();

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Send a message to the server.
     *
     * @param message
     */
    public void send(String message) {
        out.write(message);
        out.flush();
    }

    /**
     * @brief Receive a message from the server.
     */
    public String receive() {
        String result = new String();

        try {
            result = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Send a file to the server.
     *
     * @param file
     */
    public void send(File file) {

        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedInputStream fileBytes = new BufferedInputStream(fileStream);

        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[8192];
        int size;

        // Read the file and send it to the server
        try {

            while ((size = fileBytes.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }

            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Close all the streams
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileBytes.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}