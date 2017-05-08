package ch.tofind.commusica.core;

import ch.tofind.commusica.network.*;

import java.net.InetAddress;
import java.util.ArrayList;

public class ClientCore extends AbstractCore {

    //!
    private MulticastClient multicast;

    public ClientCore(String multicastAddress, int port, InetAddress interfaceToUse) {
        multicast = new MulticastClient(multicastAddress, port, interfaceToUse);

        new Thread(multicast).start();
    }

    @Override
    public void sendUnicast(String message) {

    }

    @Override
    public void sendMulticast(String message) {
        multicast.send(message);
    }

    @Override
    public void stop() {
        multicast.stop();
    }

    @Override
    public String END_OF_COMMUNICATION(ArrayList<Object> args) {
        System.out.println("End of communication client side.");
        return "";
    }

    public String TRACK_ACCEPTED(ArrayList<Object> args) {
        String result = Protocol.SEND_TRACK + Protocol.END_OF_LINE +
                12345 + Protocol.END_OF_LINE +
                Protocol.END_OF_COMMAND;
        return result;
    }

    public String TRACK_REFUSED(ArrayList<Object> args) {
        String result = Protocol.END_OF_COMMUNICATION + Protocol.END_OF_LINE +
                12345 + Protocol.END_OF_LINE +
                Protocol.END_OF_COMMAND;
        return result;
    }

    public String TRACK_SAVED(ArrayList<Object> args) {
        String result = Protocol.END_OF_COMMUNICATION + Protocol.END_OF_LINE +
                12345 + Protocol.END_OF_LINE +
                Protocol.END_OF_COMMAND;
        return result;
    }
}
