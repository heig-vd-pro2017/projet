package ch.tofind.commusica.core;

import ch.tofind.commusica.session.ServerSession;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public final class ApplicationProtocol {

    //!
    public static Integer myId = null;

    //!
    public static Integer serverId = null;

    //!
    public static InetAddress serverAddress = null;

    //!
    public static String serverName = null;

    //! Commands
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    public static final String DISCOVER_REQUEST = "DISCOVER_REQUEST";
    public static final String DISCOVER_SERVER = "DISCOVER_SERVER";
    public static final String SERVER_DISCOVERED = "SERVER_DISCOVERED";

    public static final String TRACK_REQUEST = "TRACK_REQUEST";
    public static final String TRACK_ACCEPTED = "TRACK_ACCEPTED";
    public static final String TRACK_REFUSED = "TRACK_REFUSED";
    public static final String SEND_TRACK = "SEND_TRACK";
    public static final String TRACK_SAVED = "TRACK_SAVED";

    public static final String PLAYLIST_UPDATE = "PLAYLIST_UPDATE";
    public static final String SEND_PLAYLIST_UPDATE = "SEND_PLAYLIST_UPDATE";
    public static final String SEND_TRACK_REQUEST = "SEND_TRACK_REQUEST";
}
