package ch.tofind.commusica.core;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.file.FileManager;
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
import java.nio.file.Files;
import java.util.ArrayList;

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
    private Track trackReceived;

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


        String inetaddressJson = Serialize.serialize(NetworkProtocol.interfaceToUse);
        String playlistJson = Serialize.serialize(PlaylistManager.getInstance().getPlaylist());

        String command = ApplicationProtocol.PLAYLIST_UPDATE + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                inetaddressJson + NetworkProtocol.END_OF_LINE +
                name + NetworkProtocol.END_OF_LINE +
                playlistJson + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;

        multicast.send(command);

        return "";
    }

    public String TRACK_REQUEST(ArrayList<Object> args) {
        LOG.info("In TRACK_REQUEST");

        String idString  = (String) args.remove(0);
        // remove the socket which is passed
        args.remove(0);
        String trackJson = (String) args.remove(0);

        trackReceived = Serialize.unserialize(trackJson, Track.class);

        Session session = DatabaseManager.getInstance().getSession();

        String queryString = String.format("from Track where id = '%s'", trackReceived.getId());
        Query<Track> queryId = session.createQuery(queryString, Track.class);
        queryString = String.format("from Track where title = '%s' and album = '%s' and artist = '%s' and length > '%d'" +
                " and length < '%d'", trackReceived.getTitle(), trackReceived.getAlbum(), trackReceived.getArtist(),
                trackReceived.getLength() - 5, trackReceived.getLength() + 5);
        Query<Track> queryOtherAttributes = session.createQuery(queryString, Track.class);

        String result = "";
        if (queryId.list().isEmpty() && queryOtherAttributes.list().isEmpty()) {
            LOG.info("Track not in the database.");
            result = ApplicationProtocol.TRACK_ACCEPTED + NetworkProtocol.END_OF_LINE;
        } else {
            LOG.info("Track already in the database.");
            result = ApplicationProtocol.TRACK_REFUSED + NetworkProtocol.END_OF_LINE;
        }

        result = result.concat(ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND);

        return result;
    }

    public String SEND_TRACK(ArrayList<Object> args) {
        LOG.info("In SEND_TRACK");

        String idString  = (String) args.remove(0);
        Socket socket = (Socket) args.remove(0);
        String fileSizeString = (String) args.remove(0);

        // Delegate the job to the FileManager
        String URI = "";
        try {
            int fileSize = Integer.parseInt(fileSizeString);
            System.out.println(fileSize);
            System.out.println("Delegating to FM");
            URI = FileManager.getInstance().retrieveFile(socket.getInputStream(), fileSize);

            // if the checksum of the received track doesn't correspond to the one of the track sent
            // we delete the file and send an ERROR message
            if(!FileManager.getInstance().checkFileMD5(new File(URI), trackReceived.getId())) {
                trackReceived = null;
                Files.delete(new File(URI).toPath());
                return ApplicationProtocol.ERROR + NetworkProtocol.END_OF_LINE +
                        NetworkProtocol.END_OF_COMMAND;
            }

            trackReceived.setUri(URI);
            //trackReceived
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update/Save the track in the database
        DatabaseManager.getInstance().save(trackReceived);

        // Add the track to the current Playlist
        PlaylistManager.getInstance().getPlaylist().addTrack(trackReceived);

        // Reset the track
        trackReceived = null;

        // Change to SUCCESS
        String result = ApplicationProtocol.TRACK_SAVED + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    @Override
    public void sendUnicast(InetAddress hostname, String message) {

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
