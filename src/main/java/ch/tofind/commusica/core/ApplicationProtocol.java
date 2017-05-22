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

    public static final String SEND_FIRST_CONNECTION = "SEND_FIRST_CONNECTION";

    public static final String TRACK_REQUEST = "TRACK_REQUEST";

    public static final String TRACK_ACCEPTED = "TRACK_ACCEPTED";

    public static final String TRACK_REFUSED = "TRACK_REFUSED";

    public static final String TRACK_SAVED = "TRACK_SAVED";

    public static final String SENDING_TRACK = "SENDING_TRACK";

    public static final String SEND_UPVOTE_TRACK_REQUEST = "SEND_UPVOTE_TRACK_REQUEST";

    public static final String SEND_DOWNVOTE_TRACK_REQUEST = "SEND_DOWNVOTE_TRACK_REQUEST";

    public static final String UPVOTE_TRACK_REQUEST = "UPVOTE_TRACK_REQUEST";

    public static final String DOWNVOTE_TRACK_REQUEST = "DOWNVOTE_TRACK_REQUEST";

    public static final String TRACK_DOWNVOTED = "TRACK_DOWNVOTED";

    public static final String TRACK_UPVOTED = "TRACK_UPVOTED";

    public static final String PLAYLIST_UPDATE = "PLAYLIST_UPDATE";

    public static final String NEW_ACTIVE_CLIENT = "NEW_ACTIVE_CLIENT";

    public static final String SEND_PLAYLIST_UPDATE = "SEND_PLAYLIST_UPDATE";

    public static final String SEND_TRACK_REQUEST = "SEND_TRACK_REQUEST";

    public static final String SEND_NEXT_TRACK_REQUEST = "SEND_NEXT_TRACK_REQUEST";

    public static final String NEXT_TRACK_REQUEST = "NEXT_TRACK_REQUEST";

    public static final String SEND_TURN_VOLUME_UP_REQUEST = "SEND_TURN_VOLUME_UP_REQUEST";

    public static final String TURN_VOLUME_UP_REQUEST = "TURN_VOLUME_UP_REQUEST";

    public static final String SEND_TURN_VOLUME_DOWN_REQUEST = "SEND_TURN_VOLUME_DOWN_REQUEST";

    public static final String TURN_VOLUME_DOWN_REQUEST = "TURN_VOLUME_DOWN_REQUEST";


    // Success messages

    public static final String SUCCESS_NEXT_TRACK = "Your vote for the next track has been taken in account";

    public static final String SUCCESS_VOLUME_UP = "Your vote for the volume up has been taken in account";

    public static final String SUCCESS_VOLUME_DOWN = "Your vote for the volume down has been taken in account";

    // Error messages

    public static final String ERROR_DURING_TRANSFER = "An error occurred during the file transfer";

    public static final String ERROR_FILE_CORRPUTED = "The checksum of the file received is not the same" +
            "as the one expected. The file was probably corrupted.";

    public static final String ERROR_FILE_EXTENSION = "The extension of the file couldn't get retrieve.";

    public static final String ERROR_ALREADY_UPVOTED = "You already upvoted this track!";

    public static final String ERROR_ALREADY_DOWNVOTED = "You already downvoted this track!";

    public static final String ERROR_TRACK_ID_NOT_IN_DB = "The track you asked for wasn't found in the DB";


}
