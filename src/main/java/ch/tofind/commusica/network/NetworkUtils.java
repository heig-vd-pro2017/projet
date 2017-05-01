package ch.tofind.commusica.network;

import java.net.*;
import java.util.*;

/**
 * Created by David on 23.03.2017.
 */
public class NetworkUtils {

    static public InetAddress addressOfInterface = null;

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
    static public void networkInterfaceChooser() {
        ArrayList<InetAddress> listInetAddress = new ArrayList<>();

        try {
            System.out.println("What interface do you want to choose?");
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface interface_ : Collections.list(interfaces)) {
                // we shouldn't care about loopback addresses
                if (interface_.isLoopback())
                    continue;

                // if you don't expect the interface to be up you can skip this
                // though it would question the usability of the rest of the code
                if (!interface_.isUp())
                    continue;

                // iterate over the addresses associated with the interface
                Enumeration<InetAddress> addresses = interface_.getInetAddresses();
                for (InetAddress addr : Collections.list(addresses)) {
                    // look only for ipv4 addresses
                    if (addr instanceof Inet6Address)
                        continue;
                    System.out.println(addr);
                    System.out.println(interface_.getName());
                    listInetAddress.add(addr);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if (listInetAddress.size() == 0) {
            return;
        }
        Scanner scanner = new Scanner(System.in);
        int type = 0;

        System.out.println(listInetAddress);

        while (type == 0 || type > listInetAddress.size()) {
            type = scanner.nextInt();
        }
        addressOfInterface = listInetAddress.get(type - 1);
        return;
    }


}