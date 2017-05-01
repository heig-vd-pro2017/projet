package ch.tofind.commusica.network.client;


import ch.tofind.commusica.network.NetworkUtils;
import ch.tofind.commusica.network.Protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class implementing the Runnable interface which allows a client to discover the available servers
 */
public class ClientDiscovery implements Runnable {

    private static Logger _logger = Logger.getLogger(ClientDiscovery.class.getName());

    private String address = Protocol.IP_MULTICAST_DISCOVERY;

    private MulticastSocket _socket = null;

    private ArrayList<InetAddress> serversList = new ArrayList<>();

    private InetAddress addressOfInterface;

    public ClientDiscovery() {
        this.addressOfInterface = NetworkUtils.addressOfInterface;
    }

    public void run() {
        try {
            _socket = new MulticastSocket(Protocol.PORT_MULTICAST_DISCOVERY);

            _socket.setInterface(addressOfInterface);

            InetAddress addr = InetAddress.getByName(address);

            _socket.joinGroup(addr);

            byte[] message = Protocol.DISCOVER_REQUEST.getBytes();

            DatagramPacket packet = new DatagramPacket(message, message.length, addr, Protocol.PORT_MULTICAST_DISCOVERY);
            _socket.send(packet);

            _logger.info("Waiting for response from server(s) - 3s");
            _socket.setSoTimeout(3000);
            while (true) {
                try {
                    byte[] rcvdPacketBuffer = new byte[64];
                    DatagramPacket rcvdPacket = new DatagramPacket(rcvdPacketBuffer, 64);
                    _socket.receive(rcvdPacket);

                    _logger.info("Packet received from " + rcvdPacket.getAddress().toString());

                    if (new String(rcvdPacket.getData()).trim().equals(Protocol.DISCOVER_RESPONSE)) {
                        InetAddress address = rcvdPacket.getAddress();
                        if (!serversList.contains(address)) {
                            serversList.add(address);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    break;
                }
            }

            _socket.close();

        } catch (IOException e) {
            _logger.severe(e.getMessage());
        }
    }

    public ArrayList<InetAddress> getServersList() {
        return serversList;
    }
}
