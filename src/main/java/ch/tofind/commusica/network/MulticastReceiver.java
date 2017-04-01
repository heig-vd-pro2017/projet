package ch.tofind.commusica.network;

import java.io.IOException;
import java.net.*;

/**
 * Created by David on 30.03.2017.
 */

/**
 * Class used to receive data by Multicast
 */
public class MulticastReceiver implements Runnable {

    final static String INET_ADDR = "239.192.0.1";

    public void run() {
        // Get the address that we are going to connect to.
        InetAddress address = null;
        try {
            address = InetAddress.getByName(INET_ADDR);
            MulticastSocket clientSocket = new MulticastSocket(8080);

            clientSocket.setBroadcast(true);

            // Create a buffer of bytes, which will be used to store
            // the incoming bytes containing the information from the server.
            // Since the message is small here, 256 bytes should be enough.
            byte[] buf = new byte[256];

            //Join the Multicast group.
            clientSocket.joinGroup(address);

            while (true) {
                // Receive the information and print it.
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                clientSocket.receive(msgPacket);
                String msg = new String(buf, 0, buf.length);
                System.out.println("Received msg: " + msg);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
