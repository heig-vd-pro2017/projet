package ch.tofind.commusica;

import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.network.MulticastSenderReceiver;
import ch.tofind.commusica.utils.Network;
import ch.tofind.commusica.network.Protocol;
import ch.tofind.commusica.network.client.Client;
import ch.tofind.commusica.network.server.Server;
import ch.tofind.commusica.session.SessionManager;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Commusica {

    public static String execute(String command, ArrayList<Object> args) {

        System.out.println("Controller received the following command: " + command);

        // Récupère l'ID de l'utilisateur ayant effectué la commande
        String idString = String.valueOf(args.remove(0));
        Integer user = Integer.valueOf(idString);

        // Vérifie si l'utilisateur a déjà une session ou non et la crée au besoin
        SessionManager.getInstance().store(user);

        String result = new String();

        switch (command) {

            case Protocol.TRACK_REQUEST:

                break;
            case Protocol.SEND_TRACK:
                Socket socket = (Socket)args.remove(0);

                // Delegate the job to the FileManager
                try {
                    System.out.println("Delegating to FM");
                    FileManager.getInstance().retrieveFile(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            default:
                break;
        }

        return result;
    }

    public static void main(String[] args) {

        Integer uniqueID;
        Scanner scanner = new Scanner(System.in);
        InetAddress interfaceToUse;

        TreeMap<String, InetAddress> networkInterfaces = Network.getIPv4Interfaces();

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

            interfaceToUse = networkInterfaces.get(interfaceChoice);
        } else {
            interfaceToUse = networkInterfaces.firstEntry().getValue();
        }

        uniqueID = Arrays.hashCode(Network.getMacAddress(interfaceToUse));

        int launchChoice = -1;
        while (launchChoice != 0) {

            System.out.println("How would you like to launch the program ?");
            System.out.println("  [0] Quit.");
            System.out.println("  [1] As the server.");
            System.out.println("  [2] As the client.");
            System.out.print("> ");
            launchChoice = scanner.nextInt();

            if (launchChoice == 1) { // Launch as server

                MulticastSenderReceiver multicastSenderReceiver = new MulticastSenderReceiver(Protocol.MULTICAST_ADDRESS, Protocol.MULTICAST_PORT, interfaceToUse);

                Server server = new Server(Protocol.UNICAST_PORT, Network.getAddressOfInterface());

                multicastSenderReceiver.start();
                server.start();

                int actionChoice = -1;
                while (actionChoice != 0) {
                    System.out.println("Actions");
                    System.out.println("  [0] Disconnect");
                    System.out.println("  [1] Send message via Multicast");
                    System.out.print("> ");

                    actionChoice = scanner.nextInt();

                    switch (actionChoice) {
                        case 0:
                            multicastSenderReceiver.stop();
                            server.stop();
                            break;
                        case 1:
                            multicastSenderReceiver.send("Coucou");
                            break;
                        default:
                            System.out.println("Action not supported.");
                            break;
                    }
                }

            } else if (launchChoice == 2) { // Launch as client

                MulticastSenderReceiver multicastSenderReceiver = new MulticastSenderReceiver(Protocol.MULTICAST_ADDRESS, Protocol.MULTICAST_PORT, interfaceToUse);

                InetAddress hostname = null;
                try {
                    hostname = InetAddress.getByName("localhost");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                multicastSenderReceiver.start();

                Client client = new Client(hostname, Protocol.UNICAST_PORT);

                client.connect();

                int actionChoice = -1;
                while (actionChoice != 0) {
                    System.out.println("Actions");
                    System.out.println("  [0] Disconnect");
                    System.out.println("  [1] Receive command from Multicast");
                    System.out.println("  [2] Send track to Unicast");
                    System.out.print("> ");

                    actionChoice = scanner.nextInt();

                    switch (actionChoice) {
                        case 0:
                            multicastSenderReceiver.stop();
                            String command = Protocol.END_OF_COMMUNICATION + Protocol.END_OF_LINE +
                                    uniqueID + Protocol.END_OF_LINE +
                                    Protocol.END_OF_COMMAND + Protocol.END_OF_LINE;
                            client.send(command);
                            client.disconnect();
                            break;
                        case 1:
                            String message = multicastSenderReceiver.receive();
                            System.out.println("Message from multicast: " + message);
                            break;
                        case 2:
                            String exit = Protocol.TRACK_REQUEST + Protocol.END_OF_LINE +
                                    uniqueID + Protocol.END_OF_LINE +
                                    "{json représentant la track}" + Protocol.END_OF_LINE +
                                    Protocol.END_OF_COMMAND + Protocol.END_OF_LINE;
                            client.send(exit);
                            String result = client.receive();
                            System.out.println("Resultat: " + result);
                            break;
                        default:
                            System.out.println("Action not supported.");
                            break;
                    }
                }
            }
        }
    }






/*
            // send the CONNECTION_REQUEST
            out.write(request + "\n");
            out.flush();

            // Wait for the SEND_ID request
            String input;
            while ((input = in.readLine()) != null) {
                if (input.equals(Protocol.SEND_ID)) {
                    break;
                }
            }

            // Send the hash of our MAC address
            //out.write(Integer.toString(id));
            out.write(Integer.toString(Network.hashMACAddress()) + "\n");
            out.flush();

            // wait for the acknowledge that the session is created or updated
            while ((input = in.readLine()) != null) {
                if (input.equals(Protocol.SESSION_STORED)) {
                    break;
                }
            }
            // create the playlistUpdate receiver thread and start it
            if (!isBinded) {
                new Thread(playlistUpdateReceiver).start();
                this.serverIP = serverIP;
                this.port = port;
                // We connected to the server once
                isBinded = true;
            }

            System.out.println("Client " + id + " connected and binded.");


        } catch (IOException e) {
            // we check if some resources should be closed
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    public static void main(String[] args) {
        launch(args);
    }

    public static void dropDatabase() {
        String filePath = "commusica.db";
        File dbFile = new File(filePath);

        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println("App démarrée :)");

        try {
            Configuration.getInstance().load("commusica.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }

        dropDatabase();

        Playlist playlist1 = new Playlist("Test1");
        Playlist playlist2 = new Playlist("Test2");

        Track track1 = new Track("Test1", "Test", "Test", 123, "/home/ludelafo/Music/Temp/sample1.wav");
        Track track2 = new Track("Test2", "Test", "Test", 132, "/home/ludelafo/Music/Temp/sample2.wav");
        Track track3 = new Track("Test3", "Test", "Test", 321, "/home/ludelafo/Music/Temp/sample3.wav");

        PlaylistTrack pt11 = new PlaylistTrack(playlist1, track1);
        PlaylistTrack pt12 = new PlaylistTrack(playlist1, track2);
        PlaylistTrack pt13 = new PlaylistTrack(playlist1, track3);

        PlaylistTrack pt21 = new PlaylistTrack(playlist2, track1);
        PlaylistTrack pt22 = new PlaylistTrack(playlist2, track3);

        DatabaseManager.getInstance().save(playlist1);
        DatabaseManager.getInstance().save(playlist2);

        DatabaseManager.getInstance().save(track1);
        DatabaseManager.getInstance().save(track2);
        DatabaseManager.getInstance().save(track3);

        DatabaseManager.getInstance().save(pt11);
        DatabaseManager.getInstance().save(pt12);
        DatabaseManager.getInstance().save(pt13);

        DatabaseManager.getInstance().save(pt21);
        DatabaseManager.getInstance().save(pt22);

        Player player = new Player(Double.parseDouble(Configuration.getInstance().get("DEFAULT_VOLUME_STEP")));

        PlaylistManager playlistManager = PlaylistManager.getInstance();

        playlistManager.loadPlaylist(playlist1);

        pt11.downvote();
        pt11.downvote();
        pt12.upvote();

        playlistManager.addPlaylistTrack(pt11);
        playlistManager.addPlaylistTrack(pt12);

        BorderPane root = new BorderPane() {{
            VBox vbox = new VBox() {{

                HBox hbox = new HBox() {{

                    Button playButton = new Button("Play");
                    Button pauseButton = new Button("Pause");
                    Button stopButton = new Button("Stop");
                    Button nextButton = new Button("Next");
                    Button volUpButton = new Button("Volume up");
                    Button volDownButton = new Button("Volume down");

                    playButton.setOnAction(e -> {
                        player.play();

                    });

                    pauseButton.setOnAction(e -> {
                        player.pause();

                    });

                    nextButton.setOnAction(e -> {
                        player.stop();
                        player.load();
                        player.play();
                    });

                    stopButton.setOnAction(e -> {
                        player.stop();
                    });

                    volUpButton.setOnAction(e -> {
                        player.riseVolume();
                    });

                    volDownButton.setOnAction(e -> {
                        player.lowerVolume();
                    });

                    getChildren().addAll(playButton, pauseButton, stopButton, nextButton, volUpButton, volDownButton);
                }};

                getChildren().add(hbox);

            }};

            setCenter(vbox);

        }};

        Scene scene = new Scene(root, 400, 100);

        primaryStage.setScene(scene);
        primaryStage.show();

        //UIController.launch(UIController.class);

        System.out.println("Bisoir");
        DatabaseManager.getInstance().close();
    }
    */
}
