package ch.tofind.commusica.network;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * SEE IF STILL NEEDED
 *
 * Interface used for the abstraction of the client/server over a socket and provide simple send and receive methods
 *
 */

public class Protocol {

    // IPs for Multicast
    public static final String IP_MULTICAST_DISCOVERY = "239.192.0.2";
    public static final String IP_MULTICAST_PLAYLIST_UPDATE = "239.192.0.1";

    // Ports for Multicast
    public static final int PORT_MULTICAST_DISCOVERY = 8484;
    public static final int PORT_MULTICAST_PLAYLIST_UPDATE = 8181;


    // constants for the protocol
    public static final String CONNECTION_REQUEST = "CONNECTION_REQUEST";

    public static final String PLAYLIST_UPDATED = "PLAYLIST_UPDATED";

    public static final String SEND_ID = "SEND_ID";
    public static final String SESSION_CREATED = "SESSION_CREATED";
    public static final String SESSION_UPDATED = "SESSION_UPDATED";
    public static final String SESSION_ACTIVATED = "SESSION_ACTIVATED";
    public static final String SESSION_NOT_ACTIVATED = "SESSION_NOT_ACTIVATED";
    public static final String ERROR = "ERROR";
    public static final String SEND_INFO = "SEND_INFO";
    public static final String SEND_MUSIC = "SEND_MUSIC";

    public static final String DISCOVER_REQUEST = "DISCOVER_REQUEST";
    public static final String DISCOVER_RESPONSE = "DISCOVER_RESPONSE";

    // time betwwen each playlist update from the server in ms
    public static final int TIME_PLAYLIST_UPDATE = 1000;



}
