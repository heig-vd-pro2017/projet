package ch.tofind.commusica.network.client;

import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.network.NetworkUtils;
import ch.tofind.commusica.network.Protocol;
import ch.tofind.commusica.playlist.PlaylistManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


/**
 * Created by David on 30.03.2017.
 */
public class PlaylistUpdateReceiver implements Runnable {

    final static String INET_ADDR = Protocol.IP_MULTICAST_PLAYLIST_UPDATE;

    private boolean isRunning;

    private Gson gson;


    private InetAddress addressOfInterface;

    public PlaylistUpdateReceiver() {
        this.addressOfInterface = NetworkUtils.getAddressOfInterface();
        isRunning = true;
    }

    public void run() {
        // Get the address that we are going to connect to.
        InetAddress address = null;

        gson = new GsonBuilder().create();

        try {
            address = InetAddress.getByName(INET_ADDR);
            MulticastSocket clientSocket = new MulticastSocket(Protocol.PORT_MULTICAST_PLAYLIST_UPDATE);

            clientSocket.setInterface(addressOfInterface);

            // buffer which will contain the messages received from the server
            byte[] buf = new byte[128];

            //Join the Multicast group.
            clientSocket.joinGroup(address);

            while (isRunning) {
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);

                clientSocket.receive(msgPacket);
                if (new String(buf, 0, buf.length).equals(Protocol.PLAYLIST_UPDATED)) {
                    buf = new byte[2048];
                    msgPacket = new DatagramPacket(buf, buf.length);
                    clientSocket.receive(msgPacket);

                    String playlistJson = new String(buf, 0, buf.length);

                    Playlist playlist = gson.fromJson(playlistJson, Playlist.class);
                    PlaylistManager.getInstance().loadPlaylist(playlist);
                }
            }

            clientSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        isRunning = false;
    }
}
