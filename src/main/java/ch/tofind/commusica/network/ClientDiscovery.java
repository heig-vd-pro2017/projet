package ch.tofind.commusica.network;


import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;

public class ClientDiscovery implements Runnable {

    private static Logger _logger = Logger.getLogger(ClientDiscovery.class.getName());

    private DatagramSocket _socket = null;

    private ArrayList<InetAddress> serversList = new ArrayList<>();

    public ClientDiscovery() {

    }

    public void run() {

        try {
            _socket = new DatagramSocket();
            _socket.setBroadcast(true);

            byte[] message = "DISCOVER_COMMUSICA_REQUEST".getBytes();

            _logger.info("Trying all interfaces...");
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();

                    if(broadcast == null) continue;

                    DatagramPacket packet = new DatagramPacket(message, message.length, broadcast, 8080);
                    _socket.send(packet);
                    _logger.info("Packet sent with interface " + networkInterface.getDisplayName());
                }
            }

            _logger.info("Waiting for response from server(s) - 10s");
            _socket.setSoTimeout(3000);
            while (true) {
                try {
                    byte[] rcvdPacketBuffer = new byte[64];
                    DatagramPacket rcvdPacket = new DatagramPacket(rcvdPacketBuffer, 64);
                    _socket.receive(rcvdPacket);

                    _logger.info("Packet received from " + rcvdPacket.getAddress().toString());

                    if(new String(rcvdPacket.getData()).trim().equals("DISCOVER_COMMUSICA_RESPONSE")) {
                        InetAddress address = rcvdPacket.getAddress();
                        if(!serversList.contains(address)) {
                            serversList.add(address);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    break;
                }
            }

            //System.out.printf("Available servers: %s\n", serversList.toString());

        } catch (IOException e) {
            _logger.severe(e.getMessage());
        }
    }

    public ArrayList<InetAddress> getServersList() {
        return serversList;
    }
}
