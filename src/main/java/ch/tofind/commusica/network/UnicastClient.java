package ch.tofind.commusica.network;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;

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

    //! Socket to use for the communication
    private Socket socket;

    //! Where to send the output
    private PrintWriter out;

    //! Where to read the input
    private BufferedReader in;

    public UnicastClient(InetAddress hostname, int port) {

        try {
            this.socket = new Socket(hostname, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UnicastClient(Socket socket) {

        this.socket = socket;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
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
                if (out != null) {
                    out.write(buffer, 0, size);
                }
            }

            if (out != null) {
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Close all the streams
        try {
            if (out != null) {
                out.close();
            }
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
                e.printStackTrace();
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

            if (!Objects.equals(result, "") && !Objects.equals(command, NetworkProtocol.END_OF_COMMUNICATION)) {
                // Send the result to the client
                send(result);
            }
        }

        // Close the connection
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
