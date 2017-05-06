package ch.tofind.commusica.core;

import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.network.UnicastClient;
import ch.tofind.commusica.network.Protocol;
import ch.tofind.commusica.network.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Core {

    private Server server;
    private UnicastClient client;
    private Integer id;

    public Core(Server server, Integer id) {
        this.server = server;
        this.client = null;
        this.id = id;
    }

    public Core(UnicastClient client, Integer id) {
        this.client = client;
        this.server = null;
        this.id = id;
    }

    public String execute(String command, ArrayList<Object> args) {

        System.out.println("Controller received the following command: " + command);

        // Récupère l'ID de l'utilisateur ayant effectué la commande
        String idString = String.valueOf(args.remove(0));
        Integer user = Integer.valueOf(idString);

        // Vérifie si l'utilisateur a déjà une session ou non et la crée au besoin
        //SessionManager.getInstance().store(user);

        String result = new String();

        switch (command) {

            case Protocol.TRACK_REQUEST:
                result = Protocol.TRACK_ACCEPTED + Protocol.END_OF_LINE +
                        user + Protocol.END_OF_LINE +
                        Protocol.END_OF_COMMAND;
                break;
            case Protocol.TRACK_ACCEPTED:
                result = Protocol.SEND_TRACK + Protocol.END_OF_LINE +
                        user + Protocol.END_OF_LINE +
                        Protocol.END_OF_COMMAND;
                break;
            case Protocol.TRACK_REFUSED:
                result = Protocol.END_OF_COMMUNICATION + Protocol.END_OF_LINE +
                        user + Protocol.END_OF_LINE +
                        Protocol.END_OF_COMMAND;
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
}
