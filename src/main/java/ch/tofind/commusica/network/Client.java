package ch.tofind.commusica.network;

import java.io.*;
import java.net.Socket;

/**
 * Created by David on 21.03.2017.
 */
public class Client extends NetPort implements Runnable {

    static int id = 0;
    Socket serverSocket;



    public void connect(String serverName, int port) {
        try {
            serverSocket = new Socket(serverName, port);
            this.port = port;

            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));

            String c;
            while((c = receive()) != null) {
                if(c.equals(SEND_ID)) break;
            }
            // we need to send MAC address here but for now we send id so we can test multiple clients on the same
            // computer
           // send(Integer.toString(++id));



            send(Long.toString(NetworkUtils.hashMACAddress()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void run() {
        try {

            System.out.println("\nfini lecture");

            send("42");
            //System.out.println("ID ecrit");

            in.close();
            out.close();
        } catch (IOException e) {
        }
    }
}
