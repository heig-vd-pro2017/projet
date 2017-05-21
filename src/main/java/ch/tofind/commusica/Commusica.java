package ch.tofind.commusica;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.network.NetworkProtocol;
import ch.tofind.commusica.session.ServerSessionManager;
import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.ui.UIController;
import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.SavedPlaylist;
import ch.tofind.commusica.media.Track;

import ch.tofind.commusica.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Commusica {

    private static final Logger LOG = new Logger(Commusica.class.getSimpleName());

    public static void dropDatabase() {
        String filePath = "commusica.db";
        File dbFile = new File(filePath);

        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    public static void main(String[] args) throws Exception {
        LOG.log(Logger.Level.INFO, "Starting application...");

        Scanner scanner = new Scanner(System.in);

        int startApp = -1;
        while (startApp != 0) {
            System.out.println("How would you like to launch the program ?");
            System.out.println("  [0] Quit.");
            System.out.println("  [1] User Interface.");
            System.out.println("  [2] Network mode.");
            System.out.print("> ");
            startApp = scanner.nextInt();

            if (startApp == 1) {
                dropDatabase();

                Track track1 = new Track("hashdelatrack1", "BLOOD", "Kendrick Lamar", "DAMN.", 119, "/Users/faku99/Desktop/tmp/BLOOD.mp3");
                Track track2 = new Track("hashdelatrack2", "DNA", "Kendrick Lamar", "DAMN.", 186, "/Users/faku99/Desktop/tmp/DNA.mp3");
                Track track3 = new Track("hashdelatrack3", "HUMBLE", "Kendrick Lamar", "DAMN.", 177, "/Users/faku99/Desktop/tmp/HUMBLE.mp3");

                // TODO: Saving the track into the DB should be done when it is sent/received. Am I right?
                DatabaseManager.getInstance().save(track1);
                DatabaseManager.getInstance().save(track2);
                DatabaseManager.getInstance().save(track3);

                PlaylistManager playlistManager = PlaylistManager.getInstance();

                SavedPlaylist playlist1 = playlistManager.createPlaylist("Test1");
                SavedPlaylist playlist2 = playlistManager.createPlaylist("Test2");

                playlist1.addTrack(track1);
                playlist1.addTrack(track2);
                playlist1.addTrack(track3);

                playlist2.addTrack(track2);
                playlist2.addTrack(track3);

                playlist1.getPlaylistTrack(track1).upvote();

                UIController.launch(UIController.class);

                DatabaseManager.getInstance().close();

            } else if (startApp == 2) {



                TreeMap<String, InetAddress> networkInterfaces = ch.tofind.commusica.utils.Network.getIPv4Interfaces();

                if (networkInterfaces.size() > 1) {
                    String interfaceChoice = "";
                    while (!networkInterfaces.containsKey(interfaceChoice)) {
                        System.out.println("Which interface to use for the multicast ?");
                        for (Map.Entry<String, InetAddress> networkInterface : networkInterfaces.entrySet()) {
                            System.out.println(networkInterface.getKey() + " - " + networkInterface.getValue());
                        }
                        System.out.print("> ");
                        interfaceChoice = scanner.next();
                    }

                    NetworkProtocol.interfaceToUse = networkInterfaces.get(interfaceChoice);
                } else {
                    NetworkProtocol.interfaceToUse = networkInterfaces.firstEntry().getValue();
                }

                ApplicationProtocol.myId = Arrays.hashCode(ch.tofind.commusica.utils.Network.getMacAddress(NetworkProtocol.interfaceToUse));

                int launchChoice = -1;
                while (launchChoice != 0) {

                    System.out.println("How would you like to launch the program ?");
                    System.out.println("  [0] Quit.");
                    System.out.println("  [1] As the server.");
                    System.out.println("  [2] As the client.");
                    System.out.print("> ");
                    launchChoice = scanner.nextInt();

                    if (launchChoice == 1) { // Launch as server

                        dropDatabase();

                        Core core = new Core(NetworkProtocol.interfaceToUse);
                        core.setupAsServer("Soirée de Lulu 4", NetworkProtocol.MULTICAST_ADDRESS, NetworkProtocol.MULTICAST_PORT, NetworkProtocol.UNICAST_PORT);

                        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
                        scheduledExecutorService.scheduleAtFixedRate(() -> {
                            //System.out.println(new Date() + " - Coucou");
                            core.execute(ApplicationProtocol.SEND_PLAYLIST_UPDATE, null);
                        }, 0, 5, TimeUnit.SECONDS);

                        int actionChoice = -1;
                        while (actionChoice != 0) {
                            System.out.println("Actions");
                            System.out.println("  [0] Disconnect");
                            System.out.println("  [1] Send message via Multicast");
                            System.out.println("  [2] Show current playlist");
                            System.out.print("> ");

                            actionChoice = scanner.nextInt();

                            switch (actionChoice) {
                                case 0:
                                    core.stop();
                                    scheduledExecutorService.shutdown();
                                    break;
                                case 1:
                                    String command = "Bonsoir" + NetworkProtocol.END_OF_LINE +
                                            ApplicationProtocol.myId + NetworkProtocol.END_OF_LINE +
                                            NetworkProtocol.END_OF_COMMAND;
                                    core.sendMulticast(command);
                                    break;

                                case 2:
                                    System.out.println(PlaylistManager.getInstance().getPlaylist());
                                    break;

                                default:
                                    System.out.println("Action not supported.");
                                    break;
                            }

                        }

                    } else if (launchChoice == 2) { // Launch as client

                        Core core = new Core(NetworkProtocol.interfaceToUse);
                        core.setupAsClient(NetworkProtocol.MULTICAST_ADDRESS, NetworkProtocol.MULTICAST_PORT);

                        InetAddress hostname = null;
                        try {
                            hostname = InetAddress.getByName("localhost");
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }

                        int actionChoice = -1;
                        while (actionChoice != 0) {

                            System.out.println("Actions");
                            System.out.println("  [0] Quit");
                            System.out.println("  [1] Send track to Unicast");
                            System.out.println("  [2] Connect to a server");
                            System.out.println("  [3] Upvote the track sent (do after sending a track)");
                            System.out.println("  [4] Downvote the track sent (do after sending a track)");
                            System.out.println("  [5] Ask for the next song");
                            System.out.println("  [6] Ask to turn the volume up");
                            System.out.println("  [7] Ask to turn the volume down");
                            System.out.print("> ");

                            actionChoice = scanner.nextInt();

                            switch (actionChoice) {
                                case 0:
                                    core.stop();
                                    break;
                                case 1:
                                    ArrayList<Object> uri = new ArrayList<>();

                                    //uri.add("C:\\Users\\David\\Documents\\YourFuckingMother_x_EHDE_-_Pocket_Monsters_VIP.mp3");
                                    uri.add("C:\\Users\\David\\Downloads\\02 v.m4a");

                                    // Error test with pdf
                                    //uri.add("C:\\Users\\David\\Documents\\2014_SYE-B_TE2.pdf");

                                    //uri.add("/home/ludelafo/Music/Temp/sample3.mp3");

                                    core.execute(ApplicationProtocol.SEND_TRACK_REQUEST, uri);
                                    break;

                                case 2:
                                    ServerSessionManager.getInstance().serverChooser(ServerSessionManager.getInstance().getAvailableServers());
                                    break;

                                case 3:
                                    ArrayList<Object> trackToUpvote = new ArrayList<>();

                                    // used for test!! replace this with the MD5 hash of the file you sent before
                                    // only for test purpose
                                    trackToUpvote.add("0358fd8ee66c98236318537365cebd23");

                                    core.execute(ApplicationProtocol.SEND_UPVOTE_TRACK_REQUEST, trackToUpvote);
                                    break;

                                case 4:
                                    ArrayList<Object> trackToDownVote = new ArrayList<>();

                                    // used for test!! replace this with the MD5 hash of the file you sent before
                                    // only for test purpose
                                    trackToDownVote.add("0358fd8ee66c98236318537365cebd23");

                                    core.execute(ApplicationProtocol.SEND_DOWNVOTE_TRACK_REQUEST, trackToDownVote);
                                    break;

                                case 5:
                                    core.execute(ApplicationProtocol.SEND_NEXT_TRACK_REQUEST, null);
                                    break;

                                case 6:
                                    core.execute(ApplicationProtocol.SEND_TURN_VOLUME_UP_REQUEST, null);
                                    break;

                                case 7:
                                    core.execute(ApplicationProtocol.SEND_TURN_VOLUME_DOWN_REQUEST, null);
                                    break;

                                default:
                                    System.out.println("Action not supported.");
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }
}
