package ch.tofind.commusica.network;

import java.net.InetAddress;

/**
 * This class represents the network protocol for communication.
 */
public final class NetworkProtocol {

    //! Interface to use for multicast.
    public static InetAddress interfaceToUse = null;

    //! Multicast port.
    public static final int MULTICAST_PORT = 9999;

    //! Unicast port.
    public static final int UNICAST_PORT = 9998;

    //! Multicast address.
    public static final String MULTICAST_ADDRESS = "239.192.0.2";

    //! End of line pattern.
    public static final String END_OF_LINE = "\n";

    //! End of command pattern.
    public static final String END_OF_COMMAND = "END_OF_COMMAND";

    //! End of communication pattern.
    public static final String END_OF_COMMUNICATION = "END_OF_COMMUNICATION";
}
