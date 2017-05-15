package ch.tofind.commusica.network;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.Logger.Level;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @brief This class implements the behavior of the "servants", whose
 * responsibility is to take care of clients once they have connected. This
 * is where we implement the application protocol logic, i.e. where we read
 * data sent by the client and where we generate the responses.
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

    public UnicastClient(InetAddress hostname, int port) {

        try {
            this.socket = new Socket(hostname, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.severe(e);
        }
    }

    public UnicastClient(Socket socket) {

        this.socket = socket;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOG.severe(e);
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
     * Send a file to the server.
     *
     * @param file
     */
    public void send(File file) {

        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            LOG.severe(e);
        }

        BufferedInputStream fileBytes = new BufferedInputStream(fileStream);

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            LOG.severe(e);
        }

        byte[] buffer = new byte[8192];
        int size;

        // Read the file and send it to the server
        try {

            while ((size = fileBytes.read(buffer)) != -1) {
                if (bos != null) {
                    bos.write(buffer, 0, size);
                }
            }

            if (bos != null) {
                bos.flush();
            }

        } catch (IOException e) {
            LOG.severe(e);
        }

        // Close all the streams
        /*try {
            if (bos != null) {
                bos.close();
            }

        } catch (IOException e) {
            LOG.severe(e);
        }*/

        try {
            fileBytes.close();
        } catch (IOException e) {
            LOG.severe(e);
        }

        try {
            fileStream.close();
        } catch (IOException e) {
            LOG.severe(e);
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
                LOG.severe(e);
            }

            // If one side closed the connection, we simulate an end of communication
            if (input == null) {
                commands.add(NetworkProtocol.END_OF_COMMUNICATION);
            }

            // Get the requested command
            command = commands.remove(0);

            // Prepare the args to send to the controller
            ArrayList<Object> args = new ArrayList<>(commands);

            // A VOIR POUR MODIFIER !!
            // Add the potentially arguments for specific commands
            switch (command) {
                case ApplicationProtocol.SEND_TRACK:
                    args.add(1, socket); // 1 because the 0st is the user
                    break;
            }

            // Send the command and its arguments to the controller and get the result
            String result = Core.execute(command, args);
            System.out.println(result);

            // Send the result to the client if needed
            if (!Objects.equals(result, "") && !Objects.equals(command, NetworkProtocol.END_OF_COMMUNICATION)) {
                send(result);
            }
        }

        // Close the connection
        try {
            in.close();
        } catch (IOException e) {
            LOG.severe(e);
        }

        out.close();

        try {
            socket.close();
        } catch (IOException e) {
            LOG.severe(e);
        }
    }
}
