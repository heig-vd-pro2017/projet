package ch.tofind.commusica.core;

import ch.tofind.commusica.network.MulticastClient;
import ch.tofind.commusica.network.Protocol;
import ch.tofind.commusica.network.Server;

import java.net.InetAddress;
import java.util.ArrayList;

public class ServerCore extends AbstractCore {

    //!
    MulticastClient multicast;

    //!
    Server server;

    public ServerCore(String multicastAddress, int multicastPort, InetAddress interfaceToUse, int unicastPort) {
        multicast = new MulticastClient(multicastAddress, multicastPort, interfaceToUse);

        server = new Server(unicastPort);

        new Thread(multicast).start();
        new Thread(server).start();
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
        server.stop();
    }

    @Override
    public String END_OF_COMMUNICATION(ArrayList<Object> args) {
        System.out.println("End of communication server side.");
        return "";
    }

    public String TRACK_REQUEST(ArrayList<Object> args) {
        String result = Protocol.TRACK_ACCEPTED + Protocol.END_OF_LINE +
                12345 + Protocol.END_OF_LINE +
                Protocol.END_OF_COMMAND;
        return result;
    }

    public String SEND_TRACK(ArrayList<Object> args) {
        String result = Protocol.TRACK_SAVED + Protocol.END_OF_LINE +
                12345 + Protocol.END_OF_LINE +
                Protocol.END_OF_COMMAND;
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
}
