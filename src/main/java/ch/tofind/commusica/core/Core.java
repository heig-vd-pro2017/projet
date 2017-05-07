package ch.tofind.commusica.core;

import ch.tofind.commusica.network.Protocol;
import java.net.InetAddress;
import java.util.ArrayList;

public class Core {

    //! Shared instance of the object for all the application
    private static AbstractCore instance = null;

    //! Unique ID of the Core
    private Integer id;

    //! Name of the Core
    private String name;

    //! Interface to use for Multicast
    private InetAddress interfaceToUse;

    public static String execute(String command, ArrayList<Object> args) {
        return instance.execute(command, args);
    }

    public Core(Integer id, String name, InetAddress interfaceToUse) {
        this.id = id;
        this.name = name;
        this.interfaceToUse = interfaceToUse;
    }

    public AbstractCore getInstance() {
        return instance;
    }

    public void setupAsServer() {
        instance = new ServerCore(Protocol.MULTICAST_ADDRESS, Protocol.MULTICAST_PORT, interfaceToUse, Protocol.UNICAST_PORT);
    }

    public void setupAsClient() {
        instance = new ClientCore(Protocol.MULTICAST_ADDRESS, Protocol.MULTICAST_PORT, interfaceToUse);
    }

    public void send(String message) {
        instance.send(message);
    }

    public void stop() {
        instance.stop();
    }

}
