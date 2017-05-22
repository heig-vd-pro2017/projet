package ch.tofind.commusica.core;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.file.FilesFormats;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.network.MulticastClient;
import ch.tofind.commusica.network.NetworkProtocol;
import ch.tofind.commusica.network.Server;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.session.UserSessionManager;
import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.Serialize;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @brief This class represents the server side of the application.
 */
public class ServerCore extends AbstractCore implements ICore {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(ServerCore.class.getSimpleName());

    //! Time before a session is considered inactive.
    private static final int TIME_BEFORE_PLAYLIST_UPDATE = Integer.valueOf(Configuration.getInstance().get("TIME_BEFORE_PLAYLIST_UPDATE"));

    //! Name of the server.
    private static final String name = Configuration.getInstance().get("SERVER_NAME");

    //! Client to use for multicast.
    MulticastClient multicast;

    //! The server.
    Server server;

    //! The manager to know how many users are active.
    UserSessionManager userSessionManager;

    //! Broadcast the playlist on schedule.
    ScheduledExecutorService broadcastPlaylist;

    /**
     * @brief Setup the core as a server.
     */
    public ServerCore() {

        multicast = new MulticastClient(NetworkProtocol.MULTICAST_ADDRESS, NetworkProtocol.MULTICAST_PORT, NetworkProtocol.interfaceToUse);

        server = new Server(NetworkProtocol.UNICAST_PORT);

        userSessionManager = UserSessionManager.getInstance();

        new Thread(multicast).start();
        new Thread(server).start();

        broadcastPlaylist = Executors.newScheduledThreadPool(1);
        broadcastPlaylist.scheduleAtFixedRate(() -> {
            execute(ApplicationProtocol.SEND_PLAYLIST_UPDATE, null);
        }, 0, TIME_BEFORE_PLAYLIST_UPDATE, TimeUnit.SECONDS);
    }

    public String END_OF_COMMUNICATION(ArrayList<Object> args) {
        System.out.println("End of communication server side.");
        return "";
    }


    /**
     * @brief Entry point to send the current playlist by multicast. It also sends the server InetAdress, name
     * and id.
     *
     * @param args Args of the command.
     *
     * @return An empty String
     */
    public String SEND_PLAYLIST_UPDATE(ArrayList<Object> args) {

        String inetAddressJson = Serialize.serialize(NetworkProtocol.interfaceToUse);
        String playlistJson = Serialize.serialize(PlaylistManager.getInstance().getPlaylist());

        String command = ApplicationProtocol.PLAYLIST_UPDATE + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                inetAddressJson + NetworkProtocol.END_OF_LINE +
                name + NetworkProtocol.END_OF_LINE +
                playlistJson + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;

        multicast.send(command);

        return "";
    }

    /**
     * @brief Add the user to the active clients.
     *
     * @param args Args of the command.
     *
     * @return An empty String
     */
    public String NEW_ACTIVE_CLIENT(ArrayList<Object> args) {

        LOG.info("New active user.");

        Integer userId = Integer.parseInt((String) args.remove(0));

        userSessionManager.store(userId);

        return NetworkProtocol.END_OF_COMMUNICATION + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
    }

    /**
     * @brief Method invoked when the client sends the TRACK_REQUEST command. It retrieves the Track object
     * by the args and then check if it is already stored in the database. It checks if the id (MD5 checksum
     * of the file) is already present. If not it checks if a track with the same title, artist, album and length
     * (with a 5s delta) is already stored in the database
     *
     * @param args Args of the command.
     *
     * @return TRACK_ACCEPTED command if the track is accepted, TRACK_REFUSED command otherwise
     */
    public String TRACK_REQUEST(ArrayList<Object> args) {

        LOG.info("In TRACK_REQUEST");

        Integer userId = Integer.parseInt((String) args.remove(0)); // A UTILISER POUR VERIFIER LES SESSIONS !!

        args.remove(0); // Remove the socket as it's not needed in this command

        userSessionManager.store(userId);

        Track trackToReceive = Serialize.unserialize((String) args.remove(0), Track.class);

        Session session = DatabaseManager.getInstance().getSession();

        String queryString = String.format("from Track where id = '%s'", trackToReceive.getId());

        Query<Track> queryId = session.createQuery(queryString, Track.class);

        queryString = String.format("from Track where title = '%s' and " +
                        "album = '%s' and " +
                        "artist = '%s' and " +
                        "length > '%d' and " +
                        "length < '%d'",
                trackToReceive.getTitle(),
                trackToReceive.getAlbum(),
                trackToReceive.getArtist(),
                trackToReceive.getLength() - 5,
                trackToReceive.getLength() + 5);

        Query<Track> queryOtherAttributes = session.createQuery(queryString, Track.class);

        String result;
        if (queryId.list().isEmpty() && queryOtherAttributes.list().isEmpty()) {

            LOG.info("The track is not in the system.");
            result = ApplicationProtocol.TRACK_ACCEPTED + NetworkProtocol.END_OF_LINE;

        } else {

            LOG.info("Track already in the system.");
            result = ApplicationProtocol.TRACK_REFUSED + NetworkProtocol.END_OF_LINE;

        }

        result = result.concat(ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND);

        return result;
    }

    /**
     * @brief Method invoked when the client send the SENDING_TRACK command. retrieve by the unicast socket
     * by the args and the delegate the transfer to the FileManager.
     * Once the track is received, it compare the MD5 checksum with the id of the track to receive (which is
     * its own MD5 checksum). It then checks the format and if all checks pass, it store the new track in the
     * database and rename it with it's MD5 on the disk.
     *
     * @param args Args of the command.
     *
     * @return ERROR command if a check failed. TRACK_SAVED if the track is correctly saved.
     */
    public String SENDING_TRACK(ArrayList<Object> args) {

        LOG.info("In SENDING_TRACK");

        args.remove(0); // Remove the user ID as its not needed in this command

        Socket socket = (Socket) args.remove(0);

        Integer fileSize = Integer.parseInt((String) args.remove(0));

        Track trackToReceive = Serialize.unserialize((String) args.remove(0), Track.class);

        // Retrieve the file from the network
        FileManager fileManager = FileManager.getInstance();

        String result;

        File tempFile;
        try {
            tempFile = fileManager.retrieveFile(socket.getInputStream(), fileSize);
        } catch (IOException e) {
            LOG.error(e);

            return ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
        }

        // Compare the checksums to avoid files corruption
        String tempFileChecksum = fileManager.getMD5Checksum(tempFile);

        if (!Objects.equals(tempFileChecksum, trackToReceive.getId())) {

            fileManager.delete(tempFile);

            return ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
        }

        // Get the file extension
        String fileExtension;
        try {
            fileExtension = fileManager.getFormatExtension(tempFile);
        } catch (Exception e) {
            fileManager.delete(tempFile);
            LOG.error(e);
            result = ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
            return result;
        }

        // Save the file under its new name on the filesystem
        String filename = FileManager.OUTPUT_DIRECTORY + File.separator +
                trackToReceive.getId() + "." + fileExtension;

        File newFile = new File(filename);

        fileManager.rename(tempFile, newFile);

        trackToReceive.setUri(filename);

        // delete the temporary file
        fileManager.delete(tempFile);

        // Update/Save the file in the database
        DatabaseManager.getInstance().save(trackToReceive);

        // Add the track to the current Playlist
        PlaylistManager.getInstance().getPlaylist().addTrack(trackToReceive);

        result = ApplicationProtocol.TRACK_SAVED + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    /**
     * @brief Receive the ask for an upvote on a specific track by a client
     *
     * @param args Args of the command.
     *
     * @return END_OF_COMMUNICATION command
     */
    public String UPVOTE_TRACK_REQUEST(ArrayList<Object> args) {

        LOG.info("In UPVOTE_TRACK_REQUEST");

        Integer userId = Integer.parseInt((String) args.remove(0));

        // remove the socket because we won't need it
        args.remove(0);

        String trackId = (String) args.remove(0);

        String result;

        // Check that the track has only been upvoted once by this user
        try {
            userSessionManager.upvote(userId, trackId);
        } catch (Exception e) {
            LOG.warning("This user has already upvoted this track.");

            result = ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
            return result;
        }


        // Get the track from the database
        Session session = DatabaseManager.getInstance().getSession();

        String queryString = String.format("from Track where id = '%s'", trackId);

        Query<Track> queryId = session.createQuery(queryString, Track.class);

        // If the query has no result we send the command ERROR
        if (queryId.list().isEmpty()) {
            result = ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
            return result;
        }

        Track trackToUpvote = queryId.list().get(0);

        // Update the track properties
        PlaylistTrack playlistTrackToUpvote = PlaylistManager.getInstance().getPlaylist().getPlaylistTrack(trackToUpvote);
        playlistTrackToUpvote.upvote();

        // Update the track in the database
        session.update(playlistTrackToUpvote);

        // Update the UI
        // TODO: Is it done automatically?

        // Tells the user its track has been voted
        result = ApplicationProtocol.TRACK_UPVOTED + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    /**
     * @brief Receive the ask for a downvote on a specific track by a client
     *
     * @param args Args of the command.
     *
     * @return END_OF_COMMUNICATION command
     */
    public String DOWNVOTE_TRACK_REQUEST(ArrayList<Object> args) {

        LOG.info("In DOWNVOTE_TRACK_REQUEST");

        Integer userId = Integer.parseInt((String) args.remove(0));

        args.remove(0); // Remove the socket as it is not needed in this command

        String trackId = (String) args.remove(0);

        String result;

        // Check that the track has only been downvoted once by this user
        try {
            userSessionManager.downvote(userId, trackId);
        } catch (Exception e) {
            LOG.warning("This user has already downvoted this track.");

            result = ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
            return result;
        }

        // Get the track from the database
        Session session = DatabaseManager.getInstance().getSession();

        String queryString = String.format("from Track where id = '%s'", trackId);

        Query<Track> queryId = session.createQuery(queryString, Track.class);

        // If the query has no result we send the command ERROR
        if (queryId.list().isEmpty()) {
            result = ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
            return result;
        }

        Track trackToDownvote = queryId.list().get(0);

        // Update the track properties
        PlaylistTrack playlistTrackToUpvote = PlaylistManager.getInstance().getPlaylist().getPlaylistTrack(trackToDownvote);
        playlistTrackToUpvote.downvote();

        // Update the track in the database
        session.update(playlistTrackToUpvote);

        // Update the UI
        // TODO: Is it done automatically?

        // Tells the user its track has been voted
        result = ApplicationProtocol.TRACK_DOWNVOTED + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    /**
     * @brief Receive the ask for the next song by a client.
     *
     * @param args Args of the command.
     *
     * @return SUCCESS command
     */
    public String NEXT_TRACK_REQUEST(ArrayList<Object> args) {

        LOG.info("User asked for the next song.");

        Integer userId = Integer.parseInt((String) args.remove(0));

        userSessionManager.nextTrack(userId);

        if (userSessionManager.countNextTrackRequests() > userSessionManager.countActiveSessions() / 2) {

            userSessionManager.resetNextTrackRequests();

            // TODO Changer la musique !

            LOG.info("Next song.");
        } else {
            LOG.info("User's opinion was taken into account.");
        }

        // Tells the user its opinion was taken into account
        String result = ApplicationProtocol.SUCCESS + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    /**
     * @brief Receive the ask to turn the volume up by a client.
     *
     * @param args Args of the command.
     *
     * @return SUCCESS command
     */
    public String TURN_VOLUME_UP_REQUEST(ArrayList<Object> args) {

        LOG.info("User asked to turn the volume up.");

        Integer userId = Integer.parseInt((String) args.remove(0));

        userSessionManager.turnVolumeUp(userId);

        if (userSessionManager.countTurnVolumeUpRequests() > userSessionManager.countActiveSessions() / 2) {

            userSessionManager.resetTurnVolumeUpRequests();

            // TODO Augmenter le volume !

            LOG.info("Volume turns up.");
        } else {
            LOG.info("User's opinion was taken into account.");
        }

        // Tells the user its opinion was taken into account
        String result = ApplicationProtocol.SUCCESS + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    /**
     * @brief Receive the ask to turn the volume down by a client.
     *
     * @param args Args of the command.
     *
     * @return SUCCESS command
     */
    public String TURN_VOLUME_DOWN_REQUEST(ArrayList<Object> args) {

        LOG.info("User asked to turn the volume down.");

        Integer userId = Integer.parseInt((String) args.remove(0));

        userSessionManager.turnVolumeDown(userId);

        if (userSessionManager.countTurnVolumeDownRequests() > userSessionManager.countActiveSessions() / 2) {

            userSessionManager.resetTurnVolumeDownRequests();

            // TODO Diminuer le volume !

            LOG.info("Volume turns down.");
        } else {
            LOG.info("User's opinion was taken into account.");
        }

        // Tells the user its opinion has been counted
        String result = ApplicationProtocol.SUCCESS + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    @Override
    public void sendUnicast(InetAddress hostname, String message) {
        // Do nothing
    }

    @Override
    public void sendMulticast(String message) {
        multicast.send(message);
    }

    @Override
    public void stop() {

        // Stop the network elements
        broadcastPlaylist.shutdown();
        multicast.stop();
        server.stop();

        // Delete the unplayed tracks from the database
        Session session = DatabaseManager.getInstance().getSession();
        Query query = session.createQuery("DELETE Track", Track.class);
        query.executeUpdate();

        // Delete the tracks folder
        File tracksDirectory = new File(Configuration.getInstance().get("DEFAULT_TRACKS_DIRECTORY"));
        FileManager.getInstance().delete(tracksDirectory);

        // Close the database connection
        DatabaseManager.getInstance().close();
    }
}
