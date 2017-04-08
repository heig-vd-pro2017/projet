package ch.tofind.commusica.network.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by David on 30.03.2017.
 */
public class MulticastReceiver implements Runnable {

    final static String INET_ADDR = "239.192.0.1";

    private boolean isRunning;


    private InetAddress addressOfInterface;

    public MulticastReceiver(InetAddress addressOfInterface) {
        this.addressOfInterface = addressOfInterface;
        isRunning = true;
    }

    public void run() {
        // Get the address that we are going to connect to.
        InetAddress address = null;
        try {
            address = InetAddress.getByName(INET_ADDR);
            MulticastSocket clientSocket = new MulticastSocket(8080);

            // clientSocket.setBroadcast(true);
            clientSocket.setInterface(addressOfInterface);


            // Create a buffer of bytes, which will be used to store
            // the incoming bytes containing the information from the server.
            // Since the message is small here, 256 bytes should be enough.


            byte[] buf = new byte[256];

            //Join the Multicast group.
            clientSocket.joinGroup(address);

            int count = 0;

            while (isRunning) {

                // Receive the information and print it.
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);

                clientSocket.receive(msgPacket);
                String msg = new String(buf, 0, buf.length);
                System.out.println("Received msg: " + msg);

            }

            clientSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        isRunning = false;
    }
}
