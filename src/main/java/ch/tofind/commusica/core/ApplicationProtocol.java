package ch.tofind.commusica.core;

import java.net.InetAddress;

/**
 * @brief This class represents the application protocol for communication.
 */
public final class ApplicationProtocol {

    //! The ID of the current instance.
    public static Integer myId = null;

    //! The server's ID with which we are currently connected to.
    public static Integer serverId = null;

    //! The server's address with which we are currently connected to.
    public static InetAddress serverAddress = null;

    //! The server's name with which we are currently connected to.
    public static String serverName = null;

    //! Commands
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";


    public static final String TRACK_REQUEST = "TRACK_REQUEST";
    public static final String TRACK_ACCEPTED = "TRACK_ACCEPTED";
    public static final String TRACK_REFUSED = "TRACK_REFUSED";
    public static final String SENDING_TRACK = "SENDING_TRACK";
    public static final String TRACK_SAVED = "TRACK_SAVED";

    public static final String UPVOTE_TRACK = "UPVOTE_TRACK";
    public static final String DOWNVOTE_TRACK = "DOWNVOTE_TRACK";
    public static final String UPVOTE_TRACK_REQUEST = "UPVOTE_TRACK_REQUEST";
    public static final String DOWNVOTE_TRACK_REQUEST = "DOWNVOTE_TRACK_REQUEST";


    public static final String PLAYLIST_UPDATE = "PLAYLIST_UPDATE";
    public static final String SEND_PLAYLIST_UPDATE = "SEND_PLAYLIST_UPDATE";
    public static final String SEND_TRACK_REQUEST = "SEND_TRACK_REQUEST";
}
