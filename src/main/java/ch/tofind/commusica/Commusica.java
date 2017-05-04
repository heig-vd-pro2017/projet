package ch.tofind.commusica;

import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.utils.Network;
import ch.tofind.commusica.network.Protocol;
import ch.tofind.commusica.network.client.Client;
import ch.tofind.commusica.network.server.Server;
import ch.tofind.commusica.session.SessionManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Commusica {

    public static String execute(String command, ArrayList<Object> args) {

        // Récupère l'ID de l'utilisateur ayant effectué la commande
        Integer user = (Integer) args.remove(0);

        // Vérifie si l'utilisateur a déjà une session ou non et la crée au besoin
        SessionManager.getInstance().store(user);

        switch (command) {

            case Protocol.SEND_MUSIC:
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

        return "Done";
    }

    public static void main(String... args) {

        Scanner scanner = new Scanner(System.in);

        InetAddress addressOfInterface = null;

        System.out.println("Which interface do you want to use?");
        ArrayList<NetworkInterface> interfaces = Network.networkInterfaceChooser();
        for (int i = 1; i < interfaces.size(); ++i) {
            System.out.println("[" + i + "] " + interfaces.get(i-1).getName() + ": " + Network.getInet4AddressString(Network.getInet4Address(interfaces.get(i-1))));
        }

        Network.setAddressOfInterface(Network.getInet4Address(interfaces.get(scanner.nextInt() - 1)));

        while (true) {
            int type = 0;
            while (type != 1 && type != 2) {
                System.out.println("[1] server");
                System.out.println("[2] client");

                System.out.println("Which one are you?");
                System.out.print("> ");

                type = scanner.nextInt();
            }



            if (type == 1) {
                Server server = new Server(8081, Network.getAddressOfInterface());

                int actionServer = 0;
                while (server != null) {
                    System.out.println("What do you want to do?");
                    System.out.println("[1] Disconnect");
                    System.out.print("> ");

                    actionServer = scanner.nextInt();

                    switch (actionServer) {
                        case 1:
                            server.stop();
                            server = null;
                    }
                }
            } else {
                Client client = null;
                try {
                    client = new Client(InetAddress.getByName("localhost"), 8081);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                int actionClient = 0;
                while (client != null) {
                    System.out.println("What do you want to do?");
                    System.out.println("[1] refresh server list");
                    System.out.println("[2] connect to server");
                    System.out.println("[3] Display available servers");
                    System.out.println("[4] Disconnect");
                    System.out.println("[5] Send music");

                    System.out.print("> ");

                    actionClient = scanner.nextInt();

                    switch (actionClient) {
                        case 1:
                            client.refreshServers();
                            break;
                        case 2:
                            System.out.println("To which server do you want to connect to?");
                            System.out.println(client.getServersList().toString());
                            client.connect(client.getServersList().get(scanner.nextInt() - 1), 8081);
                            break;
                        case 3:
                            System.out.println("Available servers");
                            System.out.println(client.getServersList().toString());
                            break;
                        case 4:
                            client.fullDisconnect();
                            client = null;
                            break;
                        case 5:
                            client.sendSong("C:\\Users\\David\\Documents\\YourFuckingMother_x_EHDE_-_Pocket_Monsters_VIP.mp3");
                            break;
                        default:
                            System.out.println("Action not supported ");

                    }
                }

            }
        }
    }
    /*
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
