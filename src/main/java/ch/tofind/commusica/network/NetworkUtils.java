package ch.tofind.commusica.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by David on 23.03.2017.
 */
public class NetworkUtils {

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
}