package ch.tofind.commusica.core;

import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.network.MulticastClient;
import ch.tofind.commusica.network.NetworkProtocol;
import ch.tofind.commusica.network.Server;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.Serialize;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
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

        System.out.println("In TRACK_REQUEST");

        String trackJson = (String) args.remove(0);
        trackReceived = Serialize.unserialize(trackJson, Track.class);




        // Check if the track is in the database

            // Check if the MD5 is stored in the database

                // Check if the URI is set

                    // If URI is set, return TRACK_REFUSED as the track is already on the local disk

                // Return TRACK_ACCEPTED as the track is in the database, but not in the local disk

            // Check if the metadatas are in the database

                // Check if the URI is set

                    // If URI is set, return TRACK_REFUSED as the track is already on the local disk

                // Return TRACK_ACCEPTED as the track is in the database, but not in the local disk

            // Return TRACK_ACCEPTED as the track is not present on the system

        String result = ApplicationProtocol.TRACK_ACCEPTED + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    public String SEND_TRACK(ArrayList<Object> args) {


        System.out.println("In SEND_TRACK");
        // Delegate the job to the FileManager
        String URI = "";
        try {
            int fileSize = Integer.parseInt((String) args.get(2));
            System.out.println(fileSize);
            System.out.println("Delegating to FM");
            URI = FileManager.getInstance().retrieveFile(((Socket) args.get(1)).getInputStream(), fileSize);
            // FileManager.getInstance().checkFile(File/URI, MD5)
            // if (!FileManager.getInstance().checkFile(File/URI, MD5)) {
            // return ERROR

            trackReceived.setUri(URI);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update/Save the track in the database

        // Add the track to the current Playlist

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
