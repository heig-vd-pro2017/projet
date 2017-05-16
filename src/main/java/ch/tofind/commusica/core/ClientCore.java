package ch.tofind.commusica.core;

import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.media.EphemeralPlaylist;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.network.MulticastClient;
import ch.tofind.commusica.network.NetworkProtocol;
import ch.tofind.commusica.network.UnicastClient;
import ch.tofind.commusica.session.ServerSessionManager;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.Serialize;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

/**
 * @brief This class represents the client side of the application.
 */
public class ClientCore extends AbstractCore implements ICore {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(ClientCore.class.getSimpleName());

    //! Client to use for multicast.
    private MulticastClient multicast;

    //! Client to use for unicast.
    private UnicastClient client;

    //! File to send to the server.
    private File fileToSend;

    //!
    ServerSessionManager ssm = null;

    /**
     * @brief Setup the core as a client.
     *
     * @param multicastAddress Multicast address.
     * @param port Multicast port.
     * @param interfaceToUse Interface to use for the multicast.
     */
    public ClientCore(String multicastAddress, int port, InetAddress interfaceToUse) {
        multicast = new MulticastClient(multicastAddress, port, interfaceToUse);
        new Thread(multicast).start();

        ssm = ServerSessionManager.getInstance();
    }

    public String END_OF_COMMUNICATION(ArrayList<Object> args) {
        System.out.println("End of communication client side.");
        return "";
    }

    public String PLAYLIST_UPDATE(ArrayList<Object> args) {

        String idString = (String) args.remove(0);
        String inetaddressJson = (String) args.remove(0);
        String serverName = (String) args.remove(0);
        String playlistJson = (String) args.remove(0);


        Integer id = Integer.parseInt(idString);

        if (Objects.isNull(ApplicationProtocol.serverId)) {
            ApplicationProtocol.serverId = id;
        }

        if (Objects.equals(id, ApplicationProtocol.serverId)) {
            EphemeralPlaylist playlistUpdated = Serialize.unserialize(playlistJson, EphemeralPlaylist.class);
            //PlaylistManager.getInstance().loadPlaylist(playlistUpdated);
        }

        //System.out.println("Playlist UPDATE received");
        // We add the server to the available servers list
        InetAddress ipServer = Serialize.unserialize(inetaddressJson, InetAddress.class);

        ssm.store(ipServer, serverName, id);

        return "";
    }


    public String SEND_TRACK_REQUEST(ArrayList<Object> args) {
        String fileURI = (String) args.get(0);

        fileToSend = new File(fileURI);

        // Verfification of the format
        if (FileManager.signatureChecker(FileManager.getFirstBytes(fileToSend, 16)).equals("error")) {
            return NetworkProtocol.END_OF_COMMUNICATION + NetworkProtocol.END_OF_LINE +
                    NetworkProtocol.END_OF_COMMAND;
        }

        Track track;
        String trackJson = "";


        try {
            track = new Track(AudioFileIO.read(fileToSend));

            //System.out.println(track);
            trackJson = Serialize.serialize(track);
        } catch (CannotReadException e) {
            LOG.error(e);
        } catch (IOException e) {
            LOG.error(e);
        } catch (TagException e) {
            LOG.error(e);
        } catch (ReadOnlyFileException e) {
            LOG.error(e);
        } catch (InvalidAudioFrameException e) {
            LOG.error(e);
        }


        String command = ApplicationProtocol.TRACK_REQUEST + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                trackJson + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;

        sendUnicast(ApplicationProtocol.serverAddress, command);

        return "";
    }

    public String TRACK_ACCEPTED(ArrayList<Object> args) {
        System.out.println("In TRACK_ACCEPTED");

        String result = ApplicationProtocol.SEND_TRACK + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                fileToSend.length() + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;

        client.send(result);
        client.send(fileToSend);
        return "";
    }

    public String TRACK_REFUSED(ArrayList<Object> args) {
        String result = NetworkProtocol.END_OF_COMMUNICATION + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    public String TRACK_SAVED(ArrayList<Object> args) {
        System.out.println("In TRACK_SAVED");
        String result = NetworkProtocol.END_OF_COMMUNICATION + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    @Override
    public void sendUnicast(InetAddress hostname, String message) {
        // check if a server has been set
        if (ApplicationProtocol.serverAddress != null) {
            client = new UnicastClient(hostname, NetworkProtocol.UNICAST_PORT);
            new Thread(client).start();
            client.send(message);
        } else {
            System.out.println("No server has been set...");
        }
    }

    @Override
    public void sendMulticast(String message) {
        multicast.send(message);
    }

    @Override
    public void stop() {
        multicast.stop();
    }

    /*public String DISCOVER_SERVER(ArrayList<Object> args) {
        String command = ApplicationProtocol.DISCOVER_REQUEST + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        sendMulticast(command);

        return "";
    }

    public String SERVER_DISCOVERED(ArrayList<Object> args) {
        String serverName = (String) args.remove(0);
        String serverAddressJson = (String) args.remove(0);

        InetAddress serverAddress = json.fromJson(serverAddressJson, InetAddress.class);

        availableServers.put(serverAddress, serverName);

        System.out.println("Serveur découvert ! " + serverName);

        return "";
    } */
}
