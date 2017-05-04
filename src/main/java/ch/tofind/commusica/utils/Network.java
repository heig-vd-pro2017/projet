package ch.tofind.commusica.utils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class Network {

    private static InetAddress addressOfInterface = null;

    static public int hashMACAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface nwi = NetworkInterface.getByInetAddress(address);
            byte mac[] = nwi.getHardwareAddress();
            return Arrays.hashCode(mac);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return -1;
    }

    static public void wait(int seconde) {
        try {
            Thread.sleep(1000 * seconde);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to choose which network interface you want to use. It set the static variable addressOfInterface
     */
    static public ArrayList<NetworkInterface> networkInterfaceChooser() {
        ArrayList<NetworkInterface> networkInterfaces = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface interface_ : Collections.list(interfaces)) {
                // we shouldn't care about loopback addresses
                if (interface_.isLoopback())
                    continue;

                // if you don't expect the interface to be up you can skip this
                // though it would question the usability of the rest of the code
                if (!interface_.isUp())
                    continue;
                if (getInet4Address(interface_) == null)
                    continue;
                networkInterfaces.add(interface_);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if (networkInterfaces.size() == 0) {
            return null;
        }
        return networkInterfaces;
    }


    /*
        The following methods are use to change and display the network interfaces used
     */

    /**
     * Return the Inet4Address of the interface
     *
     * @param networkInterface
     * @return Inet4Address of the interface
     */
    static public InetAddress getInet4Address(NetworkInterface networkInterface) {
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        for (InetAddress addr : Collections.list(addresses)) {
            if (addr instanceof Inet4Address) {
                // remove de '/' of the address String
                return addr;
            }
        }
        return null;
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
}






