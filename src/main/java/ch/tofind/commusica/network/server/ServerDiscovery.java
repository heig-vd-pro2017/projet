package ch.tofind.commusica.network.server;


import ch.tofind.commusica.network.Protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Logger;

public class ServerDiscovery implements Runnable {

    private String address = "239.192.0.2";

    private static ServerDiscovery _sharedInstance = null;

    private static Logger _logger = Logger.getLogger(ServerDiscovery.class.getName());

    private MulticastSocket _socket = null;

    private InetAddress addressOfInterface;

    private ServerDiscovery(InetAddress addressOfInterface) {
        this.addressOfInterface = addressOfInterface;
    }

    public static ServerDiscovery getSharedInstance(InetAddress addressOfInterface) {

        if (_sharedInstance == null) {
            _sharedInstance = new ServerDiscovery(addressOfInterface);
        }

        return _sharedInstance;
    }

    public void run() {

        try {
            _logger.info("Launching server...");

            _socket = new MulticastSocket(8484);
            _socket.setInterface(addressOfInterface);

            InetAddress addr = InetAddress.getByName(address);

            _socket.joinGroup(addr);

            _logger.info("ServerDiscovery launched!");

            while (true) {
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
                    DatagramPacket sentPacket = new DatagramPacket(sentPacketBuffer, sentPacketBuffer.length, addr, 8484);
                    _socket.send(sentPacket);
                    _logger.info("Packet sent to " + rcvPacket.getAddress().getHostAddress());
                }
            }
        } catch (IOException e) {
            _logger.severe(e.getMessage());
        }
    }
}
