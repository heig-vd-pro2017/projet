package ch.tofind.commusica.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * @brief This class can get properties about the network interfaces of the current machine.
 */
public class Network {

    /**
     * @brief Get the MAC address from a network interface.
     *
     * @param networkInterface The network interface to get the MAC address.
     *
     * @return An array of bytes representing the MAC address.
     */
    static public byte[] getMacAddress(InetAddress networkInterface) {

        byte[] macAddress = null;

        NetworkInterface networkInterfaceToGetMacAddress;
        try {
            networkInterfaceToGetMacAddress = NetworkInterface.getByInetAddress(networkInterface);
            macAddress = networkInterfaceToGetMacAddress.getHardwareAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return macAddress;
    }

    /**
     * @brief Get all the network interfaces on the current machine.
     *
     * @return The array containing all the network interfaces.
     */
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

    /**
     * @brief Get only the IPv4 interfaces on the current machine.
     *
     * @return A map with the interfaces' name and the associated network interface.
     */
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

    /**
     * @brief Get all the addresses of a given network interface.
     *
     * @param networkInterface The network interface where to get all the addresses.
     *
     * @return An array with the addresses of the network interface.
     */
    static public ArrayList<InetAddress> getInetAddresses(NetworkInterface networkInterface) {
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        return Collections.list(addresses);
    }
}