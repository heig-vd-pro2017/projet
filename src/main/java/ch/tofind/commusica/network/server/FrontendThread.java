package ch.tofind.commusica.network.server;

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
public class FrontendThread implements Runnable {

    //! Socket to use for the communication
    private ServerSocket socket;

    //! Port to use for the communication
    private int port;

    //! Tells if the receptionist is working
    private boolean isRunning;

    /**
     * @brief Constructor.
     */
    public FrontendThread(int port) {
        this.port = port;
    }

    /**
     * @brief Stop the receptionist.
     */
    public void stop() {

        isRunning = false;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        isRunning = true;

        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isRunning) {

            try {

                Socket clientSocket = socket.accept();
                new Thread(new BackendThread(clientSocket)).start();

            } catch (SocketException e) {

                if (isRunning) {
                    e.printStackTrace();
                } else {
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
