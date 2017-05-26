package ch.tofind.commusica.core;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.media.Player;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.network.MulticastClient;
import ch.tofind.commusica.network.NetworkProtocol;
import ch.tofind.commusica.network.Server;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.session.ServerSessionManager;
import ch.tofind.commusica.session.UserSessionManager;
import ch.tofind.commusica.ui.UIController;
import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.Serialize;

import javafx.application.Platform;

import javax.persistence.NoResultException;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * @brief This class represents the server side of the application.
 */
public class ServerCore extends AbstractCore implements ICore {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(ServerCore.class.getSimpleName());

    //! Time before a session is considered inactive.
    private static final int TIME_BEFORE_PLAYLIST_UPDATE = Integer.valueOf(Configuration.getInstance().get("TIME_BETWEEN_PLAYLIST_UPDATES"));

    //! Name of the server.
    private static final String name = Configuration.getInstance().get("SERVER_NAME");

    //! Client to use for multicast.
    private MulticastClient multicast;

    //! The server.
    private Server server;

    //! The manager to know how many users are active.
    private UserSessionManager userSessionManager;

    //! Broadcast the playlist on schedule.
    private ScheduledExecutorService broadcastPlaylist;

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

    /**
     * @brief What to do when there is an end of communication.
     *
     * @param args Args of the command.
     *
     * @return The result of the command.
     */
    public String END_OF_COMMUNICATION(ArrayList<Object> args) {
        LOG.info("End of communication - server side.");
        return "";
    }


    /**
     * @brief Entry point to send the current playlist by multicast. It also sends the server InetAdress, name
     * and id.
     *
     * @param args Args of the command.
     *
     * @return The result of the command.
     */
    public String SEND_PLAYLIST_UPDATE(ArrayList<Object> args) {

        String inetAddressJson = Serialize.serialize(NetworkProtocol.interfaceToUse);
        String playlistJson = Serialize.serialize(PlaylistManager.getInstance().getPlaylist());

        Platform.runLater(() -> {
            // Save the playlist into the database and refresh UI.
            PlaylistManager.getInstance().getPlaylist().save();
            UIController.getController().refreshPlaylistsList();
            UIController.getController().refreshPlaylist();
        });

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
     * @return The result of the command.
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
     * @return The result of the command.
     */
    public String TRACK_REQUEST(ArrayList<Object> args) {

        LOG.info("Client would like to know if the server accepts the track.");

        // Get the command arguments
        Integer userId = Integer.parseInt((String) args.remove(0)); // A UTILISER POUR VERIFIER LES SESSIONS !!
        args.remove(0); // Remove the socket as it's not needed in this command
        Track trackToReceive = Serialize.unserialize((String) args.remove(0), Track.class);

        // Store the user
        userSessionManager.store(userId);

        // Get the database session to query the database
        Session session = DatabaseManager.getInstance().getSession();

        String getTrackById = "FROM Track WHERE id = :id";
        Query trackById = session.createQuery(getTrackById);
        trackById.setParameter("id", trackToReceive.getId());

        Track trackInDatabase;
        try {
            trackInDatabase = (Track) trackById.getSingleResult();
        } catch (NoResultException e) {

            LOG.info("The track was not found by ID.");

            return ApplicationProtocol.TRACK_ACCEPTED + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;

        }

        // If track was not found, try another query by attributes
        if (trackInDatabase == null) {

            String getTrackByAttributes = "FROM Track WHERE title = :title AND " +
                    "artist = :artist AND " +
                    "album = :album AND " +
                    "length > :lengthMin AND " +
                    "length < :lengthMax";

            Query trackByAttributes = session.createQuery(getTrackByAttributes);

            trackByAttributes.setParameter("title", trackToReceive.getTitle());
            trackByAttributes.setParameter("artist", trackToReceive.getArtist());
            trackByAttributes.setParameter("album", trackToReceive.getAlbum());
            trackByAttributes.setParameter("lengthMin", trackToReceive.getLength() - 5);
            trackByAttributes.setParameter("lengthMax", trackToReceive.getLength() + 5);

            try {
                trackInDatabase = (Track) trackByAttributes.getSingleResult();
            } catch (NoResultException e) {

                LOG.info("The track was not found by attributes.");

                return ApplicationProtocol.TRACK_ACCEPTED + NetworkProtocol.END_OF_LINE +
                        ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                        NetworkProtocol.END_OF_COMMAND;

            }
        }

        if (Objects.isNull(trackInDatabase.getUri())) {

            LOG.info("The track was found in database but is not stored on filesystem.");

            return ApplicationProtocol.TRACK_ACCEPTED + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;

        } else {

            LOG.info("The track was found in the system, no need to send it back.");

            return ApplicationProtocol.TRACK_REFUSED + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;

        }
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
     * @return The result of the command.
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
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.ERROR_DURING_TRANSFER + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
        }

        // Compare the checksums to avoid files corruption
        String tempFileChecksum = fileManager.getMD5Checksum(tempFile);

        if (!Objects.equals(tempFileChecksum, trackToReceive.getId())) {

            fileManager.delete(tempFile);

            return ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.ERROR_DURING_TRANSFER + NetworkProtocol.END_OF_LINE +
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
                    ApplicationProtocol.ERROR_FILE_NOT_SUPPORTED + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
            return result;
        }

        // Save the file under its new name on the filesystem
        String filename = FileManager.OUTPUT_DIRECTORY + File.separator +
                trackToReceive.getId() + "." + fileExtension;

        File newFile = new File(filename);

        fileManager.rename(tempFile, newFile);

        // Delete the temporary file
        fileManager.delete(tempFile);

        // Get the track from the database
        Session session = DatabaseManager.getInstance().getSession();

        Query trackById = session.createQuery("FROM Track WHERE id = :id");
        trackById.setParameter("id", trackToReceive.getId());

        Track trackToSave;

        // Check if track in database to modify it or create a new one.
        try {

            trackToSave = (Track) trackById.getSingleResult();

        } catch (NoResultException e) {

            trackToSave = trackToReceive;

        }

        // Set additionnal properties on the file
        trackToSave.setUri(filename);
        trackToSave.setFavoritedProperty(false);

        // Save the track in the database
        DatabaseManager.getInstance().save(trackToSave);

        // Add the track to the current Playlist
        PlaylistManager.getInstance().getPlaylist().addTrack(trackToSave);

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
     * @return The result of the command.
     */
    public String UPVOTE_TRACK_REQUEST(ArrayList<Object> args) {

        LOG.info("In UPVOTE_TRACK_REQUEST");

        Integer userId = Integer.parseInt((String) args.remove(0));

        // Remove the socket because we won't need it
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
                    ApplicationProtocol.ERROR_VOTE + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
            return result;
        }

        // Get the track from the database
        Session session = DatabaseManager.getInstance().getSession();

        Query trackById = session.createQuery("FROM Track WHERE id = :id");
        trackById.setParameter("id", trackId);

        Track trackToUpvote;

        try {
            trackToUpvote = (Track) trackById.getSingleResult();
        } catch (NoResultException e) {

            LOG.info("The track was not found by ID.");

            return ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.ERROR_VOTE + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;

        }

        // Update the track properties
        PlaylistTrack playlistTrackToUpvote = PlaylistManager.getInstance().getPlaylist().getPlaylistTrack(trackToUpvote);

        // Ask the UI to execute the command when it can
        Platform.runLater(() -> playlistTrackToUpvote.upvote());

        // Update the track in the database - L'OBJET N'EXISTE PAS DANS LA DB
        //DatabaseManager.getInstance().update(playlistTrackToUpvote);

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
     * @return The result of the command.
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
                    ApplicationProtocol.ERROR_VOTE + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
            return result;
        }

        // Get the track from the database
        Session session = DatabaseManager.getInstance().getSession();

        Query trackById = session.createQuery("FROM Track WHERE id = :id");
        trackById.setParameter("id", trackId);

        Track trackToDownvote;
        try {
            trackToDownvote = (Track) trackById.getSingleResult();
        } catch (NoResultException e) {

            LOG.info("The track was not found by ID.");

            return ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.ERROR_VOTE + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;

        }

        // Update the track properties
        PlaylistTrack playlistTrackToDownvote = PlaylistManager.getInstance().getPlaylist().getPlaylistTrack(trackToDownvote);

        // Ask the UI to execute the command when it can
        Platform.runLater(() -> playlistTrackToDownvote.downvote());

        // Update the track in the database - L'OBJET N'EXISTE PAS DANS LA DB
        //DatabaseManager.getInstance().update(playlistTrackToUpvote);

        // Tells the user its track has been voted
        result = ApplicationProtocol.TRACK_DOWNVOTED + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }


    /**
     * @brief Sends a vote for the next track from the server side.
     *
     * @param args Args of the command.
     *
     * @return An empty String.
     */
    public String SEND_NEXT_TRACK_REQUEST(ArrayList<Object> args) {

        ArrayList<Object> id = new ArrayList<>();

        id.add(Integer.toString(ApplicationProtocol.myId));
        NEXT_TRACK_REQUEST(id);

        return "";
    }

    /**
     * @brief Entry point to send a play/pause vote from the server side.
     *
     * @param args Args of the command.
     *
     * @return An empty String.
     */
    public String SEND_PLAY_PAUSE_REQUEST(ArrayList<Object> args) {

        // New args array since the one passed in parameter is null
        ArrayList<Object> id = new ArrayList<>();

        id.add(Integer.toString(ApplicationProtocol.myId));

        PLAY_PAUSE_REQUEST(id);
        return "";
    }


    /**
     * @brief Entry point to send an upvote from the server side.
     *
     * @param args Args of the command.
     *
     * @return An empty String.
     */
    public String SEND_UPVOTE_TRACK_REQUEST(ArrayList<Object> args) {

        args.add(0, Integer.toString(ApplicationProtocol.myId));

        // add a null Object instead of the socket
        args.add(1, null);
        UPVOTE_TRACK_REQUEST(args);
        return "";
    }


    /**
     * @brief Entry point to send a downvote from the server side.
     *
     * @param args Args of the command.
     *
     * @return An empty String.
     */
    public String SEND_DOWNVOTE_TRACK_REQUEST(ArrayList<Object> args) {

        args.add(0, Integer.toString(ApplicationProtocol.myId));

        // add a null Object instead of the socket
        args.add(1, null);
        DOWNVOTE_TRACK_REQUEST(args);
        return "";
    }


    /**
     * @brief Entry point to send the PLAY_PAUSE_REQUEST command.
     *
     * @param args Args of the command.
     *
     * @return The result of the command.
     */
    public String PLAY_PAUSE_REQUEST(ArrayList<Object> args) {

        LOG.info("User asked to play/pause the current track.");

        Integer userId = Integer.parseInt((String) args.remove(0));

        userSessionManager.playPause(userId);

        if (userSessionManager.countPlayPauseRequests() > userSessionManager.countActiveSessions() / 2) {

            userSessionManager.resetPlayPauseRequests();

            Player player = Player.getCurrentPlayer();

            String result = "";

            // Test if the player is on pause or playing
            if (!player.isPlaying()) {

                // Ask the UI to execute the command when it can
                Platform.runLater(() -> player.play());

                LOG.info("Player starts playing.");

                result = ApplicationProtocol.PLAY + NetworkProtocol.END_OF_LINE +
                        ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                        NetworkProtocol.END_OF_COMMAND;

            } else {

                // Ask the UI to execute the command when it can
                Platform.runLater(() -> player.pause());
                LOG.info("Player stops playing.");

                result = ApplicationProtocol.PAUSE + NetworkProtocol.END_OF_LINE +
                        ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                        NetworkProtocol.END_OF_COMMAND;

            }

            sendMulticast(result);

        } else {
            LOG.info("User's opinion was taken into account.");
        }

        // Tells the user its opinion was taken into account
        return ApplicationProtocol.SUCCESS + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.SUCCESS_VOTE + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
    }

    /**
     * @brief Receive the ask for the next song by a client.
     *
     * @param args Args of the command.
     *
     * @return The result of the command.
     */
    public String NEXT_TRACK_REQUEST(ArrayList<Object> args) {

        LOG.info("User asked for the next song.");

        Integer userId = Integer.parseInt((String) args.remove(0));

        userSessionManager.nextTrack(userId);

        if (userSessionManager.countNextTrackRequests() > userSessionManager.countActiveSessions() / 2) {

            userSessionManager.resetNextTrackRequests();

            userSessionManager.resetPreviousTrackRequests();

            // Get the track from the current player

            // Update the track in the database

            // Ask the UI to execute the command when it can
            Platform.runLater(() -> Player.getCurrentPlayer().load());

            LOG.info("Next song.");
        } else {
            LOG.info("User's opinion was taken into account.");
        }

        // Tells the user its opinion was taken into account
        String result = ApplicationProtocol.SUCCESS + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.SUCCESS_VOTE + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    /**
     * @brief Entry point to send the PREVIOUS_TRACK_REQUEST command.
     *
     * @param args Args of the command.
     *
     * @return The result of the command.
     */
    public String PREVIOUS_TRACK_REQUEST(ArrayList<Object> args) {

        LOG.info("User asked for the previous song.");

        Integer userId = Integer.parseInt((String) args.remove(0));

        userSessionManager.previousTrack(userId);

        if (userSessionManager.countPreviousTrackRequests() > userSessionManager.countActiveSessions() / 2) {

            userSessionManager.resetNextTrackRequests();

            userSessionManager.resetPreviousTrackRequests();

            LOG.info("Previous song.");
        } else {
            LOG.info("User's opinion was taken into account.");
        }

        // Tells the user its opinion was taken into account
        String result = ApplicationProtocol.SUCCESS + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.SUCCESS_VOTE + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }


    public String SEND_VOLUME_UP_REQUEST(ArrayList<Object> args) {

        ArrayList<Object> id = new ArrayList<>();

        id.add(Integer.toString(ApplicationProtocol.myId));
        TURN_VOLUME_UP_REQUEST(id);

        return "";
    }


    /**
     * @brief Receive the ask to turn the volume up by a client.
     *
     * @param args Args of the command.
     *
     * @return The result of the command.
     */
    public String TURN_VOLUME_UP_REQUEST(ArrayList<Object> args) {

        LOG.info("User asked to turn the volume up.");

        Integer userId = Integer.parseInt((String) args.remove(0));

        userSessionManager.turnVolumeUp(userId);

        if (userSessionManager.countTurnVolumeUpRequests() > userSessionManager.countActiveSessions() / 2) {

            userSessionManager.resetTurnVolumeUpRequests();

            LOG.info("Volume turns up.");

            // Ask the UI to execute the command when it can
            Platform.runLater(() -> Player.getCurrentPlayer().riseVolume());

            return ApplicationProtocol.VOLUME_TURNED_UP + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;

        } else {
            LOG.info("User's opinion was taken into account.");
        }

        // Tells the user its opinion was taken into account
        String result = ApplicationProtocol.SUCCESS + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.SUCCESS_VOTE + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    public String SEND_VOLUME_DOWN_REQUEST(ArrayList<Object> args) {

        ArrayList<Object> id = new ArrayList<>();

        id.add(Integer.toString(ApplicationProtocol.myId));
        TURN_VOLUME_DOWN_REQUEST(id);

        return "";
    }

    /**
     * @brief Receive the ask to turn the volume down by a client.
     *
     * @param args Args of the command.
     *
     * @return The result of the command.
     */
    public String TURN_VOLUME_DOWN_REQUEST(ArrayList<Object> args) {

        LOG.info("User asked to turn the volume down.");

        Integer userId = Integer.parseInt((String) args.remove(0));

        userSessionManager.turnVolumeDown(userId);

        if (userSessionManager.countTurnVolumeDownRequests() > userSessionManager.countActiveSessions() / 2) {

            userSessionManager.resetTurnVolumeDownRequests();

            LOG.info("Volume turns down.");

            // Ask the UI to execute the command when it can
            Platform.runLater(() -> Player.getCurrentPlayer().lowerVolume());

            return ApplicationProtocol.VOLUME_TURNED_DOWN + NetworkProtocol.END_OF_LINE +
                    ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;

        } else {
            LOG.info("User's opinion was taken into account.");
        }

        // Tells the user its opinion has been counted
        return ApplicationProtocol.SUCCESS + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.SUCCESS_VOTE + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
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

        LOG.warning("Server shutting down...");

        // Stop the executors
        userSessionManager.stop();
        ServerSessionManager.getInstance().stop();

        // Try to stop all remaining threads
        broadcastPlaylist.shutdown();

        // Wait 5 seconds before killing everyone
        try {
            broadcastPlaylist.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error(e);
        } finally {
            if (!broadcastPlaylist.isTerminated()) {
                LOG.error("The thread pool can't be stopped !");
            }
            broadcastPlaylist.shutdownNow();
        }

        multicast.stop();
        server.stop();

        // Delete the unplayed tracks from the database
        Session session = DatabaseManager.getInstance().getSession();
        Query query;

        query = session.createQuery("DELETE Track WHERE date_played IS NULL");
        DatabaseManager.getInstance().execute(query);

        // Set the URI to null
        query = session.createQuery("UPDATE Track SET uri = NULL");
        DatabaseManager.getInstance().execute(query);

        // Delete the tracks folder
        File tracksDirectory = new File(Configuration.getInstance().get("TRACKS_DIRECTORY"));
        FileManager.getInstance().delete(tracksDirectory);

        // Close the database connection
        DatabaseManager.getInstance().close();
    }

    @Override
    public boolean isServer() {
        return true;
    }
}
