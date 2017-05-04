package ch.tofind.commusica.network.client;

import ch.tofind.commusica.utils.Network;
import ch.tofind.commusica.network.Protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @brief This class allows a client to discover the available servers.
 */
public class ServerDiscovery implements Runnable {

    //! Logger for debugging proposes.
    private static Logger LOG = Logger.getLogger(ServerDiscovery.class.getName());

    //!
    private String address = Protocol.IP_MULTICAST_DISCOVERY;

    //!
    private MulticastSocket socket = null;

    //!
    private ArrayList<InetAddress> serversList;

    //!
    private InetAddress addressOfInterface;

    public ServerDiscovery() {
        this.addressOfInterface = Network.getAddressOfInterface();
    }

    public void run() {
        try {
            serversList = new ArrayList<>();

            socket = new MulticastSocket(Protocol.PORT_MULTICAST_DISCOVERY);

            socket.setInterface(addressOfInterface);

            InetAddress addr = InetAddress.getByName(address);

            socket.joinGroup(addr);

            byte[] message = Protocol.DISCOVER_REQUEST.getBytes();

            DatagramPacket packet = new DatagramPacket(message, message.length, addr, Protocol.PORT_MULTICAST_DISCOVERY);
            socket.send(packet);

            LOG.info("Waiting for response from server(s) - 3s");
            socket.setSoTimeout(3000);
            while (true) {
                try {
                    byte[] rcvdPacketBuffer = new byte[64];
                    DatagramPacket rcvdPacket = new DatagramPacket(rcvdPacketBuffer, 64);
                    socket.receive(rcvdPacket);

                    LOG.info("Packet received from " + rcvdPacket.getAddress().toString());

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

            socket.close();

        } catch (IOException e) {
            LOG.severe(e.getMessage());
        }
    }

    public ArrayList<InetAddress> getServersList() {
        return serversList;
    }
}
