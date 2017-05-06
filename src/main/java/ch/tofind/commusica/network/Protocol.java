package ch.tofind.commusica.network;

/**
 * SEE IF STILL NEEDED
 *
 * Interface used for the abstraction of the client/server over a socket and provide simple send and receive methods
 *
 */
public final class Protocol {

    // Multicast
    public static final String MULTICAST_ADDRESS = "239.192.0.2";
    public static final int MULTICAST_PORT = 9999;

    // Unicast
    public static final int UNICAST_PORT = 9998;

    // Protocol
    public static final String END_OF_LINE = "\n";
    public static final String END_OF_COMMAND = "END_OF_COMMAND";
    public static final String END_OF_COMMUNICATION = "END_OF_COMMUNICATION";
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    // Commands
    public static final String TRACK_REQUEST = "TRACK_REQUEST";
    public static final String TRACK_ACCEPTED = "TRACK_ACCEPTED";
    public static final String TRACK_REFUSED = "TRACK_REFUSED";
    public static final String SEND_TRACK = "SEND_TRACK";
    public static final String TRACK_SAVED = "TRACK_SAVED";



    // constants for the protocol
    public static final String CONNECTION_REQUEST = "CONNECTION_REQUEST";
    public static final String RECONNECTION_REQUEST = "RECONNECTION_REQUEST";

    public static final String PLAYLIST_UPDATED = "PLAYLIST_UPDATED";

    public static final String SEND_ID = "SEND_ID";
    public static final String SESSION_STORED = "SESSION_STORED";
    public static final String SESSION_UPDATED = "SESSION_UPDATED";
    public static final String SESSION_ACTIVATED = "SESSION_ACTIVATED";
    public static final String SESSION_NOT_ACTIVATED = "SESSION_NOT_ACTIVATED";
    public static final String SEND_INFO = "SEND_INFO";

    public static final String DISCOVER_REQUEST = "DISCOVER_REQUEST";
    public static final String DISCOVER_RESPONSE = "DISCOVER_RESPONSE";


}
