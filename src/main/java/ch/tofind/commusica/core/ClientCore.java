package ch.tofind.commusica.core;

import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.network.*;
import ch.tofind.commusica.session.ServerSession;
import ch.tofind.commusica.utils.Network;
import ch.tofind.commusica.utils.Serialize;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

public class ClientCore extends AbstractCore implements ICore {

    //! Client to use for multicast.
    private MulticastClient multicast;

    //! Client to use for unicast.
    private UnicastClient client;

    //! File to send to the server.
    private File fileToSend;

    public ClientCore(String multicastAddress, int port, InetAddress interfaceToUse) {
        multicast = new MulticastClient(multicastAddress, port, interfaceToUse);
        new Thread(multicast).start();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            //System.out.println(new Date() + " - Cleaning servers list");
            Network.cleanServersList();
            //System.out.println(Network.getAvailableServers());
        }, 0, 2, TimeUnit.SECONDS);
    }

    public String END_OF_COMMUNICATION(ArrayList<Object> args) {
        System.out.println("End of communication client side.");
        return "";
    }

    public String PLAYLIST_UPDATE(ArrayList<Object> args) {

        Integer id = Integer.parseInt((String)args.get(0));

        if (Objects.isNull(ApplicationProtocol.serverId)) {
            ApplicationProtocol.serverId = id;
        }

        if (Objects.equals(id, ApplicationProtocol.serverId)) {
            Playlist playlistUpdated = Serialize.unserialize((String)args.get(3), Playlist.class);
            //PlaylistManager.getInstance().loadPlaylist(playlistUpdated);
        }

        //System.out.println("Playlist UPDATE received");
        // We add the server to the available servers list
        InetAddress ipServer = Serialize.unserialize((String)args.get(1), InetAddress.class);
        ServerSession server = new ServerSession(ipServer, (String)args.get(2), id);

        Network.addServerToServersList(server);

        return "";
    }

    // args[0] = id
    // args[1] = URI
    public String SEND_TRACK_REQUEST(ArrayList<Object> args) {
        fileToSend = new File((String)args.get(0));

        // TODO: VERIFICATION OF THE FORMAT!
        //if (!FileManager.signatureChecker(fileToSend))

        FileManager.displayMetadatas(fileToSend);

        Track track;
        String trackJson = "";

        try {
            track = new Track(AudioFileIO.read(fileToSend));
            System.out.println(track);
            trackJson = Serialize.serialize(track);
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }


        String command = ApplicationProtocol.TRACK_REQUEST + NetworkProtocol.END_OF_LINE +
                trackJson + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;

        sendUnicast(ApplicationProtocol.serverAddress ,command);

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
