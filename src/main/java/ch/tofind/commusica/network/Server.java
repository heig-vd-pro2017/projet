package ch.tofind.commusica.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * This class implements the behavior of the "receptionist", whose
 * responsibility is to listen for incoming connection requests. As soon as a
 * new client has arrived, the receptionist delegates the processing to a
 * "servant" who will execute on its own thread.
 */
public class Server implements Runnable {

    //! Socket to use for the communication
    private ServerSocket socket;

    //! Port to use for the communication
    private int port;

    //! Tells if the receptionist is working
    private boolean running;

    /**
     * @brief Constructor.
     */
    public Server(int port) {
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
                UnicastClient client = new UnicastClient(clientSocket);
                new Thread(client).start();

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
