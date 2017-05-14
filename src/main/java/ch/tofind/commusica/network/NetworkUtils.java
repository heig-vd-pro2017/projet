package ch.tofind.commusica.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class NetworkUtils {

    public static InetAddress INTERFACE_TO_USE = null;

    static public ArrayList<NetworkInterface> getNetworkInterfaces() {

        ArrayList<NetworkInterface> networkInterfaces = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface networkInterface : Collections.list(interfaces)) {



                // We shouldn't care about loopback addresses
                if (networkInterface.isLoopback())
                    continue;

                networkInterfaces.add(networkInterface);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return networkInterfaces;
    }

    static public Map<String, InetAddress> getIPv4Interfaces() {

        ArrayList<NetworkInterface> networkInterfaces = getNetworkInterfaces();

        Map<String, InetAddress> availableIPv4Interfaces = new HashMap<>();

        for (NetworkInterface networkInterface : networkInterfaces) {

            String interfaceName = networkInterface.getName();

            ArrayList<InetAddress> inetAddresses = NetworkUtils.getInetAddresses(networkInterface);

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




}
