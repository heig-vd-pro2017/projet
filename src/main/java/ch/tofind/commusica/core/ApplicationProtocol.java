package ch.tofind.commusica.core;

import ch.tofind.commusica.session.ServerSession;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public final class ApplicationProtocol {


    public static int myId;

    public static int serverId;
    public static InetAddress serverAddress;
    public static String serverName;



    //! Commands
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    public static final String DISCOVER_REQUEST = "DISCOVER_REQUEST";
    public static final String DISCOVER_SERVER = "DISCOVER_SERVER";
    public static final String SERVER_DISCOVERED = "SERVER_DISCOVERED";

    public static final String TRACK_REQUEST = "TRACK_REQUEST";
    public static final String TRACK_ACCEPTED = "TRACK_ACCEPTED";
    public static final String TRACK_REFUSED = "TRACK_REFUSED";
    public static final String SEND_TRACK = "SEND_TRACK";
    public static final String TRACK_SAVED = "TRACK_SAVED";


    public static final String PLAYLIST_UPDATE = "PLAYLIST_UPDATE";
    public static final String SEND_PLAYLIST_UPDATE = "SEND_PLAYLIST_UPDATE";
    public static final String SEND_TRACK_REQUEST = "SEND_TRACK_REQUEST";

    public static void serverChooser(Map<InetAddress, ServerSession> serverList) {
        if (!serverList.isEmpty()) {

            ArrayList<ServerSession> servers = new ArrayList<>();

            Scanner scanner = new Scanner(System.in);
            System.out.println("To which server do you want to connect?");
            int i = 1;

            for (Map.Entry<InetAddress, ServerSession> entry : serverList.entrySet()) {
                System.out.println("[" + i++ + "]" + "    " + entry.getValue());
                servers.add(entry.getValue());
            }

            int serverChoice = -1;
            while (serverChoice < 0) {
                serverChoice = scanner.nextInt();

                if (serverChoice > serverList.size()) {
                    System.out.println("Not valid!");
                    serverChoice = -1;
                }
            }
            serverChoice--;

            serverId = servers.get(serverChoice).getId();
            serverName = servers.get(serverChoice).getName();
            serverAddress = servers.get(serverChoice).getIp();
        }
    }

}
