package ch.tofind.commusica.network.server;

import ch.tofind.commusica.network.Protocol;

import java.io.IOException;
import java.net.*;

/**
 * Runnable used to send by Multicast the current state of the Playlist
 */
public class MulticastSender implements Runnable {

    final static String INET_ADDR = Protocol.IP_MULTICAST_PLAYLIST_UPDATE;

    private MulticastSocket serverSocket;

    private InetAddress addressOfInterface;

    public MulticastSender(InetAddress addressOfInterface) {
        this.addressOfInterface = addressOfInterface;
    }

    public void run() {
        // Get the address that we are going to connect to.
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(INET_ADDR);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Open a new MulticastSocket, which will be used to send the data.
        try {
            serverSocket = new MulticastSocket(Protocol.PORT_MULTICAST_PLAYLIST_UPDATE);

            serverSocket.joinGroup(addr);

            String msg = Protocol.PLAYLIST_UPDATED;

            // Create a packet that will contain the data
            // (in the form of bytes) and send it.
            DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
                    msg.getBytes().length, addr, Protocol.PORT_MULTICAST_PLAYLIST_UPDATE);
            serverSocket.send(msgPacket);
            System.out.println("Server sent packet with msg: " + msg);


            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
