package ch.tofind.commusica.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * @brief This class receives data from the server by multicast.
 */
public class MulticastSenderReceiver {

    //! Logger for debugging proposes.
    private static Logger LOG = Logger.getLogger(MulticastSenderReceiver.class.getName());

    //!
    private String multicastAddress;

    //!
    private int port;

    //!
    InetAddress interfaceToUse;

    //!
    InetAddress multicastGroup;

    //!
    private MulticastSocket socket = null;

    public MulticastSenderReceiver(String multicastAddress, int port, InetAddress interfaceToUse) {
        this.multicastAddress = multicastAddress;
        this.port = port;
        this.interfaceToUse = interfaceToUse;
    }

    public void start() {

        try {
            socket = new MulticastSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.setInterface(interfaceToUse);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            multicastGroup = InetAddress.getByName(multicastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            socket.joinGroup(multicastGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        socket.close();
    }

    public void send(String message) {

        // Transforms the message in bytes
        byte[] messageBytes = message.getBytes();

        // Prepare the packet
        DatagramPacket outPacket = new DatagramPacket(messageBytes, messageBytes.length, multicastGroup, port);

        // Send the packet
        try {
            socket.send(outPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive() {

        // Get the response
        byte[] reponse = new byte[64];
        DatagramPacket inPacket = new DatagramPacket(reponse, reponse.length);

        try {
            socket.receive(inPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the response
        return new String(inPacket.getData()).trim();
    }
}
