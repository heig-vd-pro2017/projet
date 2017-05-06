package ch.tofind.commusica.network.server;

import ch.tofind.commusica.Commusica;
import ch.tofind.commusica.network.Protocol;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @brief This class implements the behavior of the "servants", whose
 * responsibility is to take care of clients once they have connected. This
 * is where we implement the application protocol logic, i.e. where we read
 * data sent by the client and where we generate the responses.
 */
public class BackendThread implements Runnable {

    //! Socket to use for the communication
    private Socket socket;

    //! Where to send the output
    private PrintWriter out;

    //! Where to read the input
    private BufferedReader in;

    public BackendThread(Socket socket) {

        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
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
