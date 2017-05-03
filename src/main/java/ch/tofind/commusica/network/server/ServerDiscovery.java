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

    //! Logger for debugging proposes.
    private static Logger LOG = Logger.getLogger(ServerDiscovery.class.getName());

    private MulticastSocket _socket = null;

    private InetAddress addressOfInterface;

    private boolean isRunning;

    private ServerDiscovery(InetAddress addressOfInterface) {
        this.addressOfInterface = addressOfInterface;
    }

    public static ServerDiscovery getSharedInstance() {

        if (_sharedInstance == null) {
            _sharedInstance = new ServerDiscovery(NetworkUtils.getAddressOfInterface());
        }

        return _sharedInstance;
    }

    public void run() {

        isRunning = true;

        try {
            LOG.info("Launching server...");

            _socket = new MulticastSocket(Protocol.PORT_MULTICAST_DISCOVERY);
            _socket.setInterface(addressOfInterface);

            InetAddress addr = InetAddress.getByName(address);

            _socket.joinGroup(addr);

            LOG.info("ServerDiscovery launched!");

            while (isRunning) {
                LOG.info("Waiting for packet...");

                // We wait for a packet.
                byte[] rcvPacketBuffer = new byte[64];
                DatagramPacket rcvPacket = new DatagramPacket(rcvPacketBuffer, 64);
                _socket.receive(rcvPacket);


                if (new String(rcvPacket.getData()).trim().equals(Protocol.DISCOVER_REQUEST)) {
                    LOG.info("Received packet from " + rcvPacket.getAddress().getHostAddress());
                    // Send ACK packet to client.
                    byte[] sentPacketBuffer = Protocol.DISCOVER_RESPONSE.getBytes();
                    _socket.setBroadcast(false);
                    DatagramPacket sentPacket = new DatagramPacket(sentPacketBuffer, sentPacketBuffer.length, addr, Protocol.PORT_MULTICAST_DISCOVERY);
                    _socket.send(sentPacket);
                    LOG.info("Packet sent to " + rcvPacket.getAddress().getHostAddress());
                }
            }
        } catch (IOException e) {
            LOG.severe(e.getMessage());
        }
    }

    public void stop() {
        isRunning = false;
    }
}
