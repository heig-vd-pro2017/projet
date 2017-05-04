package ch.tofind.commusica.network.server;

import ch.tofind.commusica.Commusica;
import ch.tofind.commusica.network.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        // Store the commands sent by the client
        ArrayList<String> commands = new ArrayList<>();

        try {

            String input = in.readLine();

            while (input != Protocol.END_OF_COMMUNICATION) {
                commands.add(input);
                input = in.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the requested command
        String command = commands.get(0);

        // Prepare the args to send to the controller
        ArrayList<Object> args = new ArrayList<>(commands);

        // Add the potentially arguments for specific commands
        switch (command) {
            case Protocol.SEND_MUSIC:
                System.out.println("SEND_MUSIC received");
                args.add(2, socket); // 2 because the 1st is the commande, the 2nd the user
        }

        // Send the command and its arguments to the controller and get the result
        String result = Commusica.execute(command, args);

        // Send the result to the client
        out.write(result + Protocol.END_OF_COMMUNICATION);

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
