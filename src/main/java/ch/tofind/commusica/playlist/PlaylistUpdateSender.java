package ch.tofind.commusica.playlist;

/**
 * Runnable used to send by Multicast the current state of the Playlist
 */
public class PlaylistUpdateSender {

    /*
    //!
    final static String INET_ADDR = ApplicationProtocol.IP_MULTICAST_PLAYLIST_UPDATE;

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
            serverSocket = new MulticastSocket(ApplicationProtocol.PORT_MULTICAST_PLAYLIST_UPDATE);

            serverSocket.joinGroup(addr);

            while(isRunning) {
                DatagramPacket msgPacket = new DatagramPacket(ApplicationProtocol.PLAYLIST_UPDATED.getBytes(),
                        ApplicationProtocol.PLAYLIST_UPDATED.getBytes().length, addr, ApplicationProtocol.PORT_MULTICAST_PLAYLIST_UPDATE);
                serverSocket.send(msgPacket);

                // We get the current playlist and JSONify it then we send it over Multicast
                Playlist playlist = PlaylistManager.getInstance().getPlaylist();
                String msg = gson.toJson(playlist, Playlist.class);

                msgPacket = new DatagramPacket(msg.getBytes(),
                        msg.getBytes().length, addr, ApplicationProtocol.PORT_MULTICAST_PLAYLIST_UPDATE);
                serverSocket.send(msgPacket);
                //System.out.println("ServerCore sent packet with msg: " + msg);
                Thread.sleep(ApplicationProtocol.TIME_PLAYLIST_UPDATE);
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
