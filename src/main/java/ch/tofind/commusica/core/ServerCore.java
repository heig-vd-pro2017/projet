package ch.tofind.commusica.core;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.file.FilesFormats;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.network.MulticastClient;
import ch.tofind.commusica.network.NetworkProtocol;
import ch.tofind.commusica.network.Server;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.Serialize;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @brief This class represents the server side of the application.
 */
public class ServerCore extends AbstractCore implements ICore {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(ServerCore.class.getSimpleName());

    //! Name of the server.
    String name;

    //! Client to use for multicast.
    MulticastClient multicast;

    //! The server.
    Server server;

    //!
    private Track trackToReceive;

    /**
     * @brief Setup the core as a server.
     *
     * @param name Name of the server.
     * @param multicastAddress Multicast address.
     * @param multicastPort Multicast port.
     * @param interfaceToUse Interface to use for multicast.
     * @param unicastPort Unicast port.
     */
    public ServerCore(String name, String multicastAddress, int multicastPort, InetAddress interfaceToUse, int unicastPort) {

        this.name = name;

        multicast = new MulticastClient(multicastAddress, multicastPort, interfaceToUse);

        server = new Server(unicastPort);

        new Thread(multicast).start();
        new Thread(server).start();
    }

    public String END_OF_COMMUNICATION(ArrayList<Object> args) {
        System.out.println("End of communication server side.");
        return "";
    }

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

    public String TRACK_REQUEST(ArrayList<Object> args) {
        LOG.info("In TRACK_REQUEST");

        String userId = (String) args.remove(0); // A UTILISER POUR VERIFIER LES SESSIONS !!

        args.remove(0); // Remove the socket as it's not needed in this command

        trackToReceive = Serialize.unserialize((String) args.remove(0), Track.class);

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

    public String SEND_TRACK(ArrayList<Object> args) {

        LOG.info("In SEND_TRACK");

        args.remove(0); // Remove the user ID as its not needed in this command

        Socket socket = (Socket) args.remove(0);

        Integer fileSize = Integer.parseInt((String) args.remove(0));

        // Retrieve the file from the network
        FileManager fileManager = FileManager.getInstance();

        String result = "";

        File tempFile;
        try {
            tempFile = fileManager.retrieveFile(socket.getInputStream(), fileSize);
        } catch (IOException e) {
            LOG.error(e);

            return ApplicationProtocol.TRACK_REFUSED + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
        }

        // Compare the checksums to avoid files corruption
        String tempFileChecksum = fileManager.getMD5Checksum(tempFile);

        if (!Objects.equals(tempFileChecksum, trackToReceive.getId())) {

            fileManager.delete(tempFile);

            trackToReceive = null;

            return ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
        }

        // Get the file extension
        String fileExtension = null;
        try {
            fileExtension = fileManager.getFormatExtension(tempFile);
        } catch (Exception e) {
            fileManager.delete(tempFile);
            LOG.error(e);
        }


        // Check if the file is of a supported format (MP3, M4A and WAV for now)
        // if not return an ERROR command
        if (fileExtension.equals(FilesFormats.FILE_NOT_SUPPORTED)) {
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

        // Update/Save the file in the database
        DatabaseManager.getInstance().save(trackToReceive);

        // Add the track to the current Playlist
        PlaylistManager.getInstance().getPlaylist().addTrack(trackToReceive);

        // Reset the track
        trackToReceive = null;

        result = ApplicationProtocol.TRACK_SAVED + NetworkProtocol.END_OF_LINE +
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
        multicast.stop();
        server.stop();
    }
}
