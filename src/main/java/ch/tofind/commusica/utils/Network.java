package ch.tofind.commusica.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class Network {

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


}