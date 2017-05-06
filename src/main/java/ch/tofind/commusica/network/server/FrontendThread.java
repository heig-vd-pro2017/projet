package ch.tofind.commusica.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the behavior of the "receptionist", whose
 * responsibility is to listen for incoming connection requests. As soon as a
 * new client has arrived, the receptionist delegates the processing to a
 * "servant" who will execute on its own thread.
 */
public class FrontendThread implements Runnable {

    //! Socket to use for the communication
    private ServerSocket socket;

    //! Port to use for the communication
    private int port;

    //! Tells if the receptionist is working
    private boolean running;

    /**
     * @brief Constructor.
     */
    public FrontendThread(int port) {
        this.port = port;
        this.running = false;
    }

    /**
     * @brief Stop the receptionist.
     */
    public void stop() {

        running = false;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        this.running = true;

        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (running) {

            try {

                Socket clientSocket = socket.accept();

                // DOIT ENCORE AJOUTER CE THREAD A UN PULL DE THREAD
                BackendThread backendThread = new BackendThread(clientSocket);
                new Thread(backendThread).start();

            } catch (SocketException e) {

                if (running) {
                    e.printStackTrace();
                } else {
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // DOIT NETTOYER LE PULL DE THREAD POUR LES ARRETER CORRECTEMENT

    }
}
