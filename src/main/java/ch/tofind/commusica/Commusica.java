package ch.tofind.commusica;

import ch.tofind.commusica.core.ApplicationConfiguration;
import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.session.ServerSessionManager;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.utils.Logger;

import java.util.*;

public class Commusica {

    private static final Logger LOG = new Logger(Commusica.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        LOG.info("Starting application...");

        Scanner scanner = new Scanner(System.in);

        ApplicationConfiguration.configure();

        Core core = new Core();

        int launchChoice = -1;
        while (launchChoice != 0) {

            System.out.println("How would you like to launch the program ?");
            System.out.println("  [0] Quit.");
            System.out.println("  [1] As the server.");
            System.out.println("  [2] As the client.");
            System.out.print("> ");
            launchChoice = scanner.nextInt();

            if (launchChoice == 1) { // Launch as server
                core.setupAsServer();

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
                core.setupAsClient();

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

                            // id = a381d396d1e43b7fe170b6d61a2aa429
                            uri.add("C:\\Users\\David\\Documents\\YourFuckingMother_x_EHDE_-_Pocket_Monsters_VIP.mp3");
                            ///home/ludelafo/Music/Temp/sample3.mp3uri.add("C:\\Users\\David\\Downloads\\02 v.m4a");

                            // Error test with pdf
                            //uri.add("C:\\Users\\David\\Documents\\2014_SYE-B_TE2.pdf");

                            //uri.add("/home/ludelafo/Music/Temp/sample3.mp3");

                            core.execute(ApplicationProtocol.SEND_TRACK_REQUEST, uri);
                            break;

                        case 2:
                            ServerSessionManager.getInstance().serverChooser(ServerSessionManager.getInstance().getAvailableServers());
                            core.execute(ApplicationProtocol.SEND_FIRST_CONNECTION, null);
                            break;

                        case 3:
                            ArrayList<Object> trackToUpvote = new ArrayList<>();

                            // used for test!! replace this with the MD5 hash of the file you sent before
                            // only for test purpose
                            trackToUpvote.add("a381d396d1e43b7fe170b6d61a2aa429");

                            core.execute(ApplicationProtocol.SEND_UPVOTE_TRACK_REQUEST, trackToUpvote);
                            break;

                        case 4:
                            ArrayList<Object> trackToDownVote = new ArrayList<>();

                            // used for test!! replace this with the MD5 hash of the file you sent before
                            // only for test purpose
                            trackToDownVote.add("a381d396d1e43b7fe170b6d61a2aa429");

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
