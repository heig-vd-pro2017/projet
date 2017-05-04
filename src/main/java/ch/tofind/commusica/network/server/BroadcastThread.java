package ch.tofind.commusica.network.server;

import ch.tofind.commusica.network.Protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Logger;

/**
 * Class implementing the Runnable interface which send the IP address of the server by Multicast to the clients who
 * send the DISCOVER_REQUEST message. It then send a DISCOVER_RESPONSE message with it's IP.
 */
public class BroadcastThread implements Runnable {

    //!
    private static final int BUFFER_SIZE = 64;

    //!
    private InetAddress inetInterface;

    //!
    private String address;

    //! Logger for debugging proposes.
    private static Logger LOG = Logger.getLogger(BroadcastThread.class.getName());

    //!
    private MulticastSocket socket = null;

    //!
    private boolean isRunning;


    public BroadcastThread(InetAddress inetInterface, String address) {
        this.inetInterface = inetInterface;
        this.address = address;
    }

    public void run() {

        isRunning = true;

        try {
            LOG.info("Launching server...");

            socket = new MulticastSocket(Protocol.PORT_MULTICAST_DISCOVERY);
            socket.setInterface(inetInterface);

            InetAddress inetAddress = InetAddress.getByName(address);

            socket.joinGroup(inetAddress);

            LOG.info("BroadcastThread launched!");

            while (isRunning) {

                LOG.info("Waiting for packet...");

                // We wait for a packet.
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, BUFFER_SIZE);
                socket.receive(datagramPacket);

                // Get the command
                String command = new String(datagramPacket.getData()).trim();

                if (command.equals(Protocol.DISCOVER_REQUEST)) {
                    LOG.info("Received packet from " + datagramPacket.getAddress().getHostAddress());
                    // Send ACK packet to client.
                    byte[] sentPacketBuffer = Protocol.DISCOVER_RESPONSE.getBytes();
                    socket.setBroadcast(false);
                    DatagramPacket sentPacket = new DatagramPacket(sentPacketBuffer, sentPacketBuffer.length, inetAddress, Protocol.PORT_MULTICAST_DISCOVERY);
                    socket.send(sentPacket);
                    LOG.info("Packet sent to " + datagramPacket.getAddress().getHostAddress());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        isRunning = false;
    }
}
