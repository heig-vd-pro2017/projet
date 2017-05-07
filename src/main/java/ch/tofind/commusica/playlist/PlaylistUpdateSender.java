package ch.tofind.commusica.playlist;

import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.utils.Network;
import ch.tofind.commusica.network.Protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Runnable used to send by Multicast the current state of the Playlist
 */
public class PlaylistUpdateSender {

    /*
    //!
    final static String INET_ADDR = Protocol.IP_MULTICAST_PLAYLIST_UPDATE;

    //!
    private MulticastSocket serverSocket;

    //!
    private Gson gson;

    //!
    private InetAddress addressOfInterface;

    //!
    private boolean isRunning;

    //!
    private static PlaylistUpdateSender sharedInstance;


    public static PlaylistUpdateSender getSharedInstance() {

        if (sharedInstance == null) {
            if (Network.getAddressOfInterface() != null) {
                sharedInstance = new PlaylistUpdateSender(Network.getAddressOfInterface());
            } else {
                System.out.println("No interface address set.");
            }
        }
        return sharedInstance;
    }

    private PlaylistUpdateSender(InetAddress addressOfInterface) {
        this.addressOfInterface = addressOfInterface;
    }

    public void run() {

        isRunning = true;

        // Get the address that we are going to connect to.
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(INET_ADDR);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        gson = new GsonBuilder().create();

        // Open a new MulticastSocket, which will be used to send the data.
        try {
            serverSocket = new MulticastSocket(Protocol.PORT_MULTICAST_PLAYLIST_UPDATE);

            serverSocket.joinGroup(addr);

            while(isRunning) {
                DatagramPacket msgPacket = new DatagramPacket(Protocol.PLAYLIST_UPDATED.getBytes(),
                        Protocol.PLAYLIST_UPDATED.getBytes().length, addr, Protocol.PORT_MULTICAST_PLAYLIST_UPDATE);
                serverSocket.send(msgPacket);

                // We get the current playlist and JSONify it then we send it over Multicast
                Playlist playlist = PlaylistManager.getInstance().getPlaylist();
                String msg = gson.toJson(playlist, Playlist.class);

                msgPacket = new DatagramPacket(msg.getBytes(),
                        msg.getBytes().length, addr, Protocol.PORT_MULTICAST_PLAYLIST_UPDATE);
                serverSocket.send(msgPacket);
                //System.out.println("ServerCore sent packet with msg: " + msg);
                Thread.sleep(Protocol.TIME_PLAYLIST_UPDATE);
            }
            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        isRunning = false;
    }
    */
}
