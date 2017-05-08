package ch.tofind.commusica.network;

import ch.tofind.commusica.Commusica;
import ch.tofind.commusica.core.Core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @brief This class receives data from the server by multicast.
 */
public class MulticastClient implements Runnable {

    //! Logger for debugging proposes.
    private static Logger LOG = Logger.getLogger(MulticastClient.class.getName());

    //!
    private int port;

    //!
    private InetAddress multicastGroup;

    //!
    private MulticastSocket socket;

    //!
    private boolean running;

    public MulticastClient(String multicastAddress, int port, InetAddress interfaceToUse) {
        this.port = port;
        this.running = false;

        try {
            this.socket = new MulticastSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.socket.setInterface(interfaceToUse);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            this.multicastGroup = InetAddress.getByName(multicastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            this.socket.joinGroup(multicastGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        running = true;

        // Get the response
        byte[] reponse = new byte[64];
        DatagramPacket in = new DatagramPacket(reponse, reponse.length);

        String command = null;
        String input = null;

        while (running) {

            // Store the commands sent by the client
            ArrayList<String> commands = new ArrayList<>();

            try {

                socket.receive(in);

                byte[] rawData = in.getData();

                String data = new String(rawData).trim();

                BufferedReader reader = new BufferedReader(new StringReader(data));

                input = reader.readLine();

                while (input != null && !input.equals(Protocol.END_OF_COMMAND)) {
                    commands.add(input);
                    input = reader.readLine();
                }

                reader.close();

            } catch (SocketException e) {
                input = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (input == null) {
                break;
            }

            // Get the requested command
            command = commands.remove(0);

            // Prepare the args to send to the controller
            ArrayList<Object> args = new ArrayList<>(commands);

            // Send the command and its arguments to the controller and get the result
            String result = Core.execute(command, args);

            if (!result.equals("") && command != Protocol.END_OF_COMMUNICATION) {
                send(result);
            }
        }
    }

    public void stop() {
        running = false;
        socket.close();
    }

    public void send(String message) {

        // Transforms the message in bytes
        byte[] messageBytes = message.getBytes();

        // Prepare the packet
        DatagramPacket out = new DatagramPacket(messageBytes, messageBytes.length, multicastGroup, port);

        // Send the packet
        try {
            socket.send(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}