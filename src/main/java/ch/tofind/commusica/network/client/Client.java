package ch.tofind.commusica.network.client;

import ch.tofind.commusica.Commusica;
import ch.tofind.commusica.network.Protocol;

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
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @brief This class represents a network client.
 */
public class Client implements Runnable {

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

        try {
            socket = new Socket(hostname, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Send a message to the server.
     *
     * @param command
     */
    public void send(String command) {
        out.write(command + Protocol.END_OF_LINE);
        out.flush();
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

    @Override
    public void run() {

        String command = null;
        String input = null;

        while (!Protocol.END_OF_COMMUNICATION.equals(command)) {

            // Store the commands sent by the client
            ArrayList<String> commands = new ArrayList<>();

            try {

                input = in.readLine();

                while (input != null && !Protocol.END_OF_COMMAND.equals(input)) {
                    commands.add(input);
                    input = in.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            // If one side closed the connection, we simulate an end of communication
            if (input == null) {
                commands.add(Protocol.END_OF_COMMUNICATION);
                commands.add("-1");
            }

            // Get the requested command
            command = commands.remove(0);

            // Prepare the args to send to the controller
            ArrayList<Object> args = new ArrayList<>(commands);

            // Add the potentially arguments for specific commands
            switch (command) {
                case Protocol.SEND_TRACK:
                    args.add(1, socket); // 1 because the 0st is the user
                    break;
            }

            // Send the command and its arguments to the controller and get the result
            String result = Commusica.execute(command, args);

            if (!"".equals(result) && command != Protocol.END_OF_COMMUNICATION) {
                // Send the result to the client
                out.println(result);
                out.flush();
            }
        }

        // Close the connexion
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.close();

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}