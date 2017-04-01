package ch.tofind.commusica.network;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

public class ServerDiscovery implements Runnable {

    private static ServerDiscovery _sharedInstance = null;

    private static Logger _logger = Logger.getLogger(ServerDiscovery.class.getName());

    private DatagramSocket _socket = null;

    private ServerDiscovery() {

    }

    public static ServerDiscovery getSharedInstance() {

        if (_sharedInstance == null) {
            _sharedInstance = new ServerDiscovery();
        }

        return _sharedInstance;
    }

    public void run() {
        int packetCount = 0;

        try {
            _logger.info("Launching server...");

            _socket = new DatagramSocket(8080, InetAddress.getByName("0.0.0.0"));
            System.out.println(_socket.getInetAddress());
            _socket.setBroadcast(true);

            _logger.info("ServerDiscovery launched!");

            while (true) {
                _logger.info("Waiting for packet...");

                // We wait for a packet.
                byte[] rcvPacketBuffer = new byte[64];
                DatagramPacket rcvPacket = new DatagramPacket(rcvPacketBuffer, 64);
                _socket.receive(rcvPacket);

                // Packet received.
                /*_logger.info(String.format("Yay! Packet %d has been received!", ++packetCount));

                // Retrieve packet properties.
                StringBuilder rcvPacketProperties = new StringBuilder();
                rcvPacketProperties.append(String.format(" sender: %s\n", rcvPacket.getAddress().getHostAddress()));
                rcvPacketProperties.append(String.format(" length: %d\n", rcvPacket.getLength()));
                rcvPacketProperties.append(String.format(" offset: %d\n", rcvPacket.getOffset()));
                rcvPacketProperties.append(String.format(" data  : %s\n", new String(rcvPacket.getData()).trim()));

                // Display packet properties.
                _logger.info("Packet properties:");
                _logger.info(rcvPacketProperties.toString());*/

                if(new String(rcvPacket.getData()).trim().equals("DISCOVER_COMMUSICA_REQUEST")) {
                    _logger.info("Received packet from " + rcvPacket.getAddress().getHostAddress());
                    // Send ACK packet to client.
                    byte[] sentPacketBuffer = "DISCOVER_COMMUSICA_RESPONSE".getBytes();
                    _socket.setBroadcast(false);
                    DatagramPacket sentPacket = new DatagramPacket(sentPacketBuffer, sentPacketBuffer.length, rcvPacket.getAddress(), rcvPacket.getPort());
                    _socket.send(sentPacket);
                    _logger.info("Packet sent to " + rcvPacket.getAddress().getHostAddress());
                }
            }
        } catch (IOException e) {
            _logger.severe(e.getMessage());
        }
    }
}
