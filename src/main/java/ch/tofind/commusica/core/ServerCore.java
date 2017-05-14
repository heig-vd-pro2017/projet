package ch.tofind.commusica.core;

import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.network.MulticastClient;
import ch.tofind.commusica.network.NetworkProtocol;
import ch.tofind.commusica.network.NetworkUtils;
import ch.tofind.commusica.network.Server;
import ch.tofind.commusica.playlist.PlaylistManager;

import ch.tofind.commusica.utils.Serialize;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ServerCore extends AbstractCore implements ICore {

    //! Name of the server.
    String name;

    //! Client to use for multicast.
    MulticastClient multicast;

    //! The server.
    Server server;

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


        String inetaddressJson = Serialize.serialize(NetworkUtils.INTERFACE_TO_USE);
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
        String result = ApplicationProtocol.TRACK_ACCEPTED + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    public String SEND_TRACK(ArrayList<Object> args) {

        System.out.println("In SEND_TRACK");
        // Delegate the job to the FileManager
        try {
            System.out.println("Delegating to FM");
            FileManager.getInstance().retrieveFile(((Socket)args.get(1)).getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = ApplicationProtocol.TRACK_SAVED + NetworkProtocol.END_OF_LINE +
                ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
                /*
                Socket socket = (Socket)args.remove(0);

                // Delegate the job to the FileManager
                try {
                    System.out.println("Delegating to FM");
                    FileManager.getInstance().retrieveFile(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
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
