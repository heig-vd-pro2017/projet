package ch.tofind.commusica.network.server;

import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.network.Protocol;
import ch.tofind.commusica.playlist.PlaylistManager;


import java.io.IOException;
import java.net.*;
import com.google.gson.*;

/**
 * Runnable used to send by Multicast the current state of the Playlist
 */
public class MulticastSender implements Runnable {

    final static String INET_ADDR = Protocol.IP_MULTICAST_PLAYLIST_UPDATE;

    private MulticastSocket serverSocket;

    private InetAddress addressOfInterface;
    private Gson gson;

    public MulticastSender(InetAddress addressOfInterface) {
        this.addressOfInterface = addressOfInterface;
    }

    public void run() {
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

            DatagramPacket msgPacket = new DatagramPacket(Protocol.PLAYLIST_UPDATED.getBytes(),
                    Protocol.PLAYLIST_UPDATED.getBytes().length, addr, Protocol.PORT_MULTICAST_PLAYLIST_UPDATE);
            serverSocket.send(msgPacket);

            // We get the current playlist and JSONify it then we send it over Multicast
            Playlist playlist = PlaylistManager.getInstance().getPlaylist();
            String msg = gson.toJson(playlist, Playlist.class);

            msgPacket = new DatagramPacket(msg.getBytes(),
                    msg.getBytes().length, addr, Protocol.PORT_MULTICAST_PLAYLIST_UPDATE);
            serverSocket.send(msgPacket);
            System.out.println("Server sent packet with msg: " + msg);


            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
