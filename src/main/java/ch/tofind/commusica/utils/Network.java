package ch.tofind.commusica.utils;

import ch.tofind.commusica.session.ServerSession;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class Network {

    //! time in seconds before a server is considered inactive
    private static int TIME_INACTIVE_SERVER = 10;

    private static InetAddress addressOfInterface = null;

    public static Integer hashedMacAddress = null;

    private static Map<InetAddress, ServerSession> availableServers = new HashMap<>();

    static public byte[] getMacAddress(InetAddress hostname) {

        byte[] macAddress = null;

        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByInetAddress(hostname);
            macAddress = networkInterface.getHardwareAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return macAddress;
    }

    static public ArrayList<NetworkInterface> getNetworkInterfaces() {

        ArrayList<NetworkInterface> networkInterfaces = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface networkInterface : Collections.list(interfaces)) {

                // We shouldn't care about loopback addresses
                if (networkInterface.isLoopback())
                    continue;

                // We shouldn't care about disconnected links
                if (!networkInterface.isUp())
                    continue;

                networkInterfaces.add(networkInterface);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return networkInterfaces;
    }

    static public TreeMap<String, InetAddress> getIPv4Interfaces() {

        ArrayList<NetworkInterface> networkInterfaces = getNetworkInterfaces();

        TreeMap<String, InetAddress> availableIPv4Interfaces = new TreeMap<>();

        for (NetworkInterface networkInterface : networkInterfaces) {

            String interfaceName = networkInterface.getName();

            ArrayList<InetAddress> inetAddresses = Network.getInetAddresses(networkInterface);

            for (InetAddress address : inetAddresses) {

                if (address instanceof Inet4Address) {
                    availableIPv4Interfaces.put(interfaceName, address);
                }
            }
        }

        return availableIPv4Interfaces;
    }

    static public ArrayList<InetAddress> getInetAddresses(NetworkInterface networkInterface) {
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        return Collections.list(addresses);
    }

    public static String getInet4AddressString(InetAddress address) {
        return address.toString().substring(1);
    }

    public static InetAddress getAddressOfInterface() {
        return addressOfInterface;
    }

    public static void setAddressOfInterface(InetAddress addr) {
        addressOfInterface = addr;
    }

    public static NetworkInterface getCurrentNetworkInterface() {
        try {
            if (addressOfInterface == null) {
                return new MulticastSocket(8585).getNetworkInterface();
            } else {
                return NetworkInterface.getByInetAddress(addressOfInterface);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<InetAddress, ServerSession> getAvailableServers() {
        return availableServers;
    }

    public static void addServerToServersList(ServerSession serverSession) {
        availableServers.put(serverSession.getIp(), serverSession);
    }

    public static void cleanServersList() {
        for (Map.Entry<InetAddress, ServerSession> entry : availableServers.entrySet()) {
            // if the server hasn't been refreshed in the last 10s
            if (new Date().getTime() - entry.getValue().getUpdated().getTime() > 1000 * TIME_INACTIVE_SERVER) {

                availableServers.remove(entry.getValue().getIp());
            }
        }
    }
}