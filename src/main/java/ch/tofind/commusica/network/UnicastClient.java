package ch.tofind.commusica.network;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.utils.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @brief This class represents an unicast client.
 */
public class UnicastClient implements Runnable {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(UnicastClient.class.getSimpleName());

    //! Socket to use for the communication.
    private Socket socket;

    //! Where to send the output.
    private PrintWriter out;

    //! Where to read the input.
    private BufferedReader in;

    /**
     * @brief Create a unicast client by hostname and port.
     *
     * @param hostname Where to send information.
     * @param port On which port we send information.
     */
    public UnicastClient(InetAddress hostname, int port) {

        try {
            this.socket = new Socket(hostname, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    /**
     * @brief Create a unicast client using an already existing socket.
     *
     * @param socket The socket which has already been connected to a server.
     */
    public UnicastClient(Socket socket) {

        this.socket = socket;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    /**
     * @brief Send a message to the server.
     *
     * @param command
     */
    public void send(String command) {
        out.write(command + NetworkProtocol.END_OF_LINE);
        out.flush();
    }

    /**
     * @brief Send a file to the server.
     *
     * @param file The file to send to the server.
     */
    public void send(File file) {

        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            LOG.error(e);
        }

        BufferedInputStream fileBytes = new BufferedInputStream(fileStream);

        BufferedOutputStream outputFileBytes = null;
        try {
            outputFileBytes = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            LOG.error(e);
        }

        byte[] buffer = new byte[8192];
        int size;

        // Read the file and send it to the server
        try {

            while ((size = fileBytes.read(buffer)) != -1) {
                if (outputFileBytes != null) {
                    outputFileBytes.write(buffer, 0, size);
                }
            }

            if (outputFileBytes != null) {
                outputFileBytes.flush();
            }

        } catch (IOException e) {
            LOG.error(e);
        }

        // Close all the streams
        try {
            fileBytes.close();
        } catch (IOException e) {
            LOG.error(e);
        }

        try {
            fileStream.close();
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public void run() {

        String command = null;
        String input = null;

        while (!Objects.equals(command, NetworkProtocol.END_OF_COMMUNICATION)) {

            // Store the commands sent by the client
            ArrayList<String> commands = new ArrayList<>();

            try {

                input = in.readLine();

                while (input != null && !Objects.equals(input, NetworkProtocol.END_OF_COMMAND)) {
                    commands.add(input);
                    input = in.readLine();
                }

            } catch (IOException e) {
                LOG.error(e);
            }

            // If one side closed the connection, we simulate an end of communication
            if (input == null) {
                String myId = String.valueOf(ApplicationProtocol.myId);

                commands.add(NetworkProtocol.END_OF_COMMUNICATION);
                commands.add(myId);
            }

            // Get the requested command
            command = commands.remove(0);

            // Prepare the args to send to the controller
            ArrayList<Object> args = new ArrayList<>(commands);

            // Add the current socket used for the communication
            args.add(1, socket); // 1 because the 0st is the user

            // Send the command and its arguments to the controller and get the result
            String result = Core.execute(command, args);

            // Send the result to the client if needed
            if (!Objects.equals(result, "") && !Objects.equals(command, NetworkProtocol.END_OF_COMMUNICATION)) {
                send(result);
            }
        }

        // Close the connection
        try {
            in.close();
        } catch (IOException e) {
            LOG.error(e);
        }

        out.close();

        try {
            socket.close();
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}
