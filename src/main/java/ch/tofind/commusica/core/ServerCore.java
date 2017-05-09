package ch.tofind.commusica.core;

import ch.tofind.commusica.network.MulticastClient;
import ch.tofind.commusica.network.NetworkProtocol;
import ch.tofind.commusica.network.Server;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;

public class ServerCore extends AbstractCore implements ICore {

    //! Name of the server.
    String name;

    //! Multicast client to send commands via multicast.
    MulticastClient multicast;

    //! The server.
    Server server;

    public ServerCore(String name, String multicastAddress, int multicastPort, InetAddress interfaceToUse, int unicastPort) {

        this.name = name;

        multicast = new MulticastClient(multicastAddress, multicastPort, interfaceToUse);

        server = new Server(unicastPort);

        new Thread(multicast).start();
        new Thread(server).start();
    }

    public String END_OF_COMMUNICATION(ArrayList<Object> args) {
        System.out.println("End of communication server side.");
        return "";
    }

    public String DISCOVER_SERVER(ArrayList<Object> args) {
        String result = ApplicationProtocol.SERVER_DISCOVERED + NetworkProtocol.END_OF_LINE +
                "Soirée de ouf malade" + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;

        return result;
    }

    public String TRACK_REQUEST(ArrayList<Object> args) {
        String result = ApplicationProtocol.TRACK_ACCEPTED + NetworkProtocol.END_OF_LINE +
                12345 + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    public String SEND_TRACK(ArrayList<Object> args) {
        String result = ApplicationProtocol.TRACK_SAVED + NetworkProtocol.END_OF_LINE +
                12345 + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
                /*
                Socket socket = (Socket)args.remove(0);

                // Delegate the job to the FileManager
                try {
                    System.out.println("Delegating to FM");
                    FileManager.getInstance().retrieveFile(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
        return result;
    }

    @Override
    public String commandNotFound() {
        return END_OF_COMMUNICATION(null);
    }

    @Override
    public void sendUnicast(InetAddress hostname, String message) {

    }

    @Override
    public void sendMulticast(String message) {
        multicast.send(message);
    }

    @Override
    public void stop() {
        multicast.stop();
        server.stop();
    }
}
