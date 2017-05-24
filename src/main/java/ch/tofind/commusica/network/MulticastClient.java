package ch.tofind.commusica.network;

import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @brief This class a multicast client.
 */
public class MulticastClient implements Runnable {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(MulticastClient.class.getSimpleName());

    //! Port to use.
    private int port;

    //! Multicast address.
    private InetAddress multicastGroup;

    //! Socket to use.
    private MulticastSocket socket;

    //! Tells if the client is running.
    private boolean running;

    /**
     * @brief MulticastClient constructor.
     *
     * @param multicastAddress Multicast address to use.
     * @param port Port to use for the communication.
     * @param interfaceToUse Network interface to use.
     */
    public MulticastClient(String multicastAddress, int port, InetAddress interfaceToUse) {
        this.port = port;
        this.running = false;

        try {
            socket = new MulticastSocket(port);
        } catch (IOException e) {
            LOG.error(e);
        }

        try {
            socket.setInterface(interfaceToUse);
            socket.setLoopbackMode(false);
        } catch (SocketException e) {
            LOG.error(e);
        }

        try {
            multicastGroup = InetAddress.getByName(multicastAddress);
        } catch (UnknownHostException e) {
            LOG.error(e);
        }

        try {
            socket.joinGroup(multicastGroup);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    /**
     * @brief Stop the multicast client.
     */
    public void stop() {
        running = false;
        socket.close();
    }

    /**
     * @brief Send a message by multicast.
     *
     * @param message The message to send.
     */
    public void send(String message) {

        // Transforms the message in bytes
        byte[] messageBytes = message.getBytes();

        // Prepare the packet
        DatagramPacket out = new DatagramPacket(messageBytes, messageBytes.length, multicastGroup, port);

        // Send the packet
        try {
            socket.send(out);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public void run() {

        running = true;

        // Get the response
        byte[] reponse = new byte[8192];
        DatagramPacket in = new DatagramPacket(reponse, reponse.length);

        String command;
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

                while ((input != null) && !Objects.equals(input, NetworkProtocol.END_OF_COMMAND)) {
                    commands.add(input);
                    input = reader.readLine();
                }

                reader.close();

            } catch (SocketException e) {
                // Do nothing and continue
                continue;
            } catch (IOException e) {
                LOG.error(e);
            }

            // Get the requested command
            command = commands.remove(0);

            // Prepare the args to send to the controller
            ArrayList<Object> args = new ArrayList<>(commands);

            // Send the command and its arguments to the controller and get the result
            String result = Core.execute(command, args);

            if (!Objects.equals(result, "")) {
                send(result + NetworkProtocol.END_OF_LINE);
            }
        }
    }
}
