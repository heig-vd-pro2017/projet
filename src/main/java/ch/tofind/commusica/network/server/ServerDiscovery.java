package ch.tofind.commusica.network.server;


import ch.tofind.commusica.network.NetworkUtils;
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
public class ServerDiscovery implements Runnable {

    private String address = Protocol.IP_MULTICAST_DISCOVERY;

    private static ServerDiscovery _sharedInstance = null;

    private static Logger _logger = Logger.getLogger(ServerDiscovery.class.getName());

    private MulticastSocket _socket = null;

    private InetAddress addressOfInterface;

    private boolean isRunning;

    private ServerDiscovery(InetAddress addressOfInterface) {
        this.addressOfInterface = addressOfInterface;
    }

    public static ServerDiscovery getSharedInstance() {

        if (_sharedInstance == null) {
            _sharedInstance = new ServerDiscovery(NetworkUtils.addressOfInterface);
        }

        return _sharedInstance;
    }

    public void run() {

        isRunning = true;

        try {
            _logger.info("Launching server...");

            _socket = new MulticastSocket(Protocol.PORT_MULTICAST_DISCOVERY);
            _socket.setInterface(addressOfInterface);

            InetAddress addr = InetAddress.getByName(address);

            _socket.joinGroup(addr);

            _logger.info("ServerDiscovery launched!");

            while (isRunning) {
                _logger.info("Waiting for packet...");

                // We wait for a packet.
                byte[] rcvPacketBuffer = new byte[64];
                DatagramPacket rcvPacket = new DatagramPacket(rcvPacketBuffer, 64);
                _socket.receive(rcvPacket);


                if (new String(rcvPacket.getData()).trim().equals(Protocol.DISCOVER_REQUEST)) {
                    _logger.info("Received packet from " + rcvPacket.getAddress().getHostAddress());
                    // Send ACK packet to client.
                    byte[] sentPacketBuffer = Protocol.DISCOVER_RESPONSE.getBytes();
                    _socket.setBroadcast(false);
                    DatagramPacket sentPacket = new DatagramPacket(sentPacketBuffer, sentPacketBuffer.length, addr, Protocol.PORT_MULTICAST_DISCOVERY);
                    _socket.send(sentPacket);
                    _logger.info("Packet sent to " + rcvPacket.getAddress().getHostAddress());
                }
            }
        } catch (IOException e) {
            _logger.severe(e.getMessage());
        }
    }

    public void stop() {
        isRunning = false;
    }
}
