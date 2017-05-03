package ch.tofind.commusica.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This inner class implements the behavior of the "receptionist", whose
 * responsibility is to listen for incoming connection requests. As soon as a
 * new client has arrived, the receptionist delegates the processing to a
 * "servant" who will execute on its own thread.
 */
public class ReceptionistWorker implements Runnable {

    //! Logger for debugging proposes.
    final static Logger LOG = Logger.getLogger(Server.class.getName());

    //!
    private boolean isRunning;

    //!
    private ServerSocket serverSocket;

    //!
    private int port;

    public ReceptionistWorker(int port) {
        isRunning = false;
        this.port = port;
    }

    @Override
    public void run() {

        isRunning = true;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

        while (true) {
            LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", port);
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ServantWorker(clientSocket)).start();

            } catch (SocketException se) {
                if (!isRunning) {
                    LOG.log(Level.INFO, "Receptionist Thread stopping.");
                    return;
                } else {
                    se.printStackTrace();
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void stop() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
