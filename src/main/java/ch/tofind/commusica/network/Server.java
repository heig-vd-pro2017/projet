package ch.tofind.commusica.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    //!
    private ExecutorService threadPool;

    /**
     * @brief Constructor.
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
            e.printStackTrace();
        }

        // Try to stop all remaining threads
        threadPool.shutdown();

        // Wait 5 seconds before killing everyone
        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!threadPool.isTerminated()) {
                System.err.println("cancel non-finished tasks");
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
            e.printStackTrace();
        }

        while (running) {

            try {

                Socket clientSocket = socket.accept();

                System.out.println("New client arrived");
                // Create a client and add it to the thread pool
                Thread client = new Thread(new UnicastClient(clientSocket));
                //client.start();
                threadPool.submit(client);

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
    }
}
