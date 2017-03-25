package ch.tofind.commusica.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by David on 25.03.2017.
 */
public class MulticastSocketClient implements Runnable {

    // this address should be BETWEEN 224.0.0. to 239.255.255.255. Here we use 224.0.0.3 arbitrary
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8888;
    private volatile boolean running = true;
    //id used for test to distinguish Multicast clients
    private int id;

    public MulticastSocketClient(int id) {
        this.id = id;
    }

    public void stop() {
        running = false;
    }

    public void run()  {
        // Get the address that we are going to connect to.
        InetAddress address = null;
        try {
            address = InetAddress.getByName(INET_ADDR);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Create a buffer of bytes, which will be used to store
        // the incoming bytes containing the information from the server.
        // The size need to be re-determined after, this is for tests
        byte[] buf = new byte[512];

        // Create a new Multicast socket (that will allow other sockets/programs
        // to join it as well).
        try  {
            MulticastSocket clientSocket = new MulticastSocket(PORT);
            //Join the Multicast group.
            clientSocket.joinGroup(address);

            while (running) {
                // Receive the information and print it.
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                clientSocket.receive(msgPacket);

                String msg = new String(buf, 0, buf.length);
                System.out.println("Socket " + id + " received msg: " + msg);
            }
            clientSocket.close();
            System.out.println("Multicast client " + id + " stopped");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}