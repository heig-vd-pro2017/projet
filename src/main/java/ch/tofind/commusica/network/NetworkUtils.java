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

    static public long hashMACAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface nwi = NetworkInterface.getByInetAddress(address);
            byte mac[] = nwi.getHardwareAddress();
            return Long.hashCode(concatenateMAC(mac));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return -1;
    }

    static private long concatenateMAC(byte[] mac) {
        long result = 0;
        for(byte b : mac) {
            result = result << 8;
            result |= b;
        }
        return result;
    }
}