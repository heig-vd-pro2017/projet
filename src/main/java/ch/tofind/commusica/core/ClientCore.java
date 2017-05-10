package ch.tofind.commusica.core;

import ch.tofind.commusica.network.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientCore extends AbstractCore implements ICore {

    //!
    private MulticastClient multicast;

    //!
    private UnicastClient client;

    //!
    private Gson json;

    //! A FINIR / VOIR SI NÉCESSAIRE
    private Map<InetAddress, String> availableServers;

    public ClientCore(String multicastAddress, int port, InetAddress interfaceToUse) {
        multicast = new MulticastClient(multicastAddress, port, interfaceToUse);
        new Thread(multicast).start();

        json = new GsonBuilder().create();

        availableServers = new HashMap<>();
    }

    public Map<InetAddress, String> getAvailableServers() {
        return availableServers;
    }

    public String END_OF_COMMUNICATION(ArrayList<Object> args) {
        System.out.println("End of communication client side.");
        return "";
    }

    public String DISCOVER_SERVER(ArrayList<Object> args) {
        String command = ApplicationProtocol.DISCOVER_SERVER + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        sendMulticast(command);

        return "";
    }

    public String SERVER_DISCOVERED(ArrayList<Object> args) {
        String serverName = (String) args.remove(0);
        String serverAddressJson = (String) args.remove(0);

        InetAddress serverAddress = json.fromJson(serverAddressJson, InetAddress.class);

        availableServers.put(serverAddress, serverName);

        System.out.println("Serveur découvert ! " + serverName);

        return "";
    }

    public String TRACK_ACCEPTED(ArrayList<Object> args) {
        String result = ApplicationProtocol.SEND_TRACK + NetworkProtocol.END_OF_LINE +
                12345 + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;

        //client.send(file);
        return result;
    }

    public String TRACK_REFUSED(ArrayList<Object> args) {
        String result = NetworkProtocol.END_OF_COMMUNICATION + NetworkProtocol.END_OF_LINE +
                12345 + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    public String TRACK_SAVED(ArrayList<Object> args) {
        String result = NetworkProtocol.END_OF_COMMUNICATION + NetworkProtocol.END_OF_LINE +
                12345 + NetworkProtocol.END_OF_LINE +
                NetworkProtocol.END_OF_COMMAND;
        return result;
    }

    @Override
    public String commandNotFound() {
        return END_OF_COMMUNICATION(null);
    }

    @Override
    public void sendUnicast(InetAddress hostname, String message) {
        client = new UnicastClient(hostname, NetworkProtocol.UNICAST_PORT);
        new Thread(client).start();
        client.send(message);
    }

    @Override
    public void sendMulticast(String message) {
        multicast.send(message);
    }

    @Override
    public void stop() {
        multicast.stop();
    }
}
