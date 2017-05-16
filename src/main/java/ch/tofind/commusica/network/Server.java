package ch.tofind.commusica.network;

import ch.tofind.commusica.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @brief This class represents a server.
 */
public class Server implements Runnable {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(Server.class.getSimpleName());

    //! Socket to use for the communication.
    private ServerSocket socket;

    //! Port to use for the communication.
    private int port;

    //! Tells if the receptionist is working.
    private boolean running;

    //! Thread pool for the clients
    private ExecutorService threadPool;

    /**
     * @brief Constructor.
     *
     * @param port Port to use for the communication.
     */
    public Server(int port) {
        this.port = port;
        this.running = false;
        this.threadPool = Executors.newCachedThreadPool();
    }

    /**
     * @brief Stop the receptionist.
     */
    public void stop() {

        running = false;

        try {
            socket.close();
        } catch (IOException e) {
            LOG.error(e);
        }

        // Try to stop all remaining threads
        threadPool.shutdown();

        // Wait 5 seconds before killing everyone
        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error(e);
        } finally {
            if (!threadPool.isTerminated()) {
                LOG.error("The thread pool can't be stopped !");
            }
            threadPool.shutdownNow();
        }
    }

    @Override
    public void run() {

        this.running = true;

        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            LOG.error(e);
        }

        while (running) {

            try {

                Socket clientSocket = socket.accept();

                LOG.info("New client arrived.");

                Thread client = new Thread(new UnicastClient(clientSocket));
                threadPool.submit(client);

            } catch (SocketException e) {

                if (running) {
                    LOG.error(e);
                } else {
                    break;
                }

            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }
}
