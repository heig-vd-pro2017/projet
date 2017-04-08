package ch.tofind.commusica.network.server;

import ch.tofind.commusica.network.Protocol;

import java.io.IOException;
import java.net.*;

/**
 * Created by David on 30.03.2017.
 */
public class MulticastSender extends Thread {

    final static String INET_ADDR = "239.192.0.1";

    MulticastSocket serverSocket;

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
            serverSocket = new MulticastSocket(8181);

            //serverSocket.setBroadcast(true);

            // if I set it manually it works (here goes the IP of my PC n°1)
            serverSocket.setInterface(addressOfInterface);

            serverSocket.joinGroup(addr);

            String msg = Protocol.PLAYLIST_UPDATED;

            // Create a packet that will contain the data
            // (in the form of bytes) and send it.
            DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
                    msg.getBytes().length, addr, 8080);
            serverSocket.send(msgPacket);
            System.out.println("Server sent packet with msg: " + msg);


            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
