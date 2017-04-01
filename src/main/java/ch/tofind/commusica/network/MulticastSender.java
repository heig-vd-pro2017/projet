package ch.tofind.commusica.network;

import java.io.IOException;
import java.net.*;

/**
 * Created by David on 30.03.2017.
 */

/**
 * Class used to send data using Multicast
 */
public class MulticastSender extends Thread {

    final static String INET_ADDR = "239.192.0.1";

    MulticastSocket serverSocket;

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
            serverSocket = new MulticastSocket();

            serverSocket.setBroadcast(true);

            // if I set it manually it works (here goes the IP of my PC n°1)
            //serverSocket.setInterface(InetAddress.getByName("IP of my first PC"));

            serverSocket.joinGroup(addr);

            for (int i = 0; i < 100; i++) {
                String msg = "Sent message no " + i;

                // Create a packet that will contain the data
                // (in the form of bytes) and send it.
                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
                        msg.getBytes().length, addr, 8080);
                serverSocket.send(msgPacket);

                System.out.println("Server sent packet with msg: " + msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
