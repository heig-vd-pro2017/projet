package ch.tofind.commusica.network.server;

import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.network.Protocol;
import ch.tofind.commusica.network.Session;
import ch.tofind.commusica.network.SessionManager;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {
    private SessionManager sm = new SessionManager();
    private int port;

    private InetAddress addressOfInterface;

    private ScheduledExecutorService scheduledExecutorService;
    private ReceptionistWorker receptionist;
    private Runnable serverDiscovery;
    private Runnable playlistUpdater;

    final static Logger LOG = Logger.getLogger(Server.class.getName());

    /**
     * Constructor
     *
     * @param port the port to listen on
     */
    public Server(int port, InetAddress addressOfInterface) {
        this.port = port;
        this.addressOfInterface = addressOfInterface;
    }

    /**
     * This method initiates the process. The server creates a socket and binds it
     * to the previously specified port. It then waits for clients in a infinite
     * loop.
     */
    public void serveClients() {
        LOG.info("Starting the Receptionist Worker on a new thread...");
        scheduledExecutorService =
                Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    sm.deleteObsoleteSessions();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        receptionist = new ReceptionistWorker();
        new Thread(receptionist).start();
        new Thread(ServerDiscovery.getSharedInstance()).start();
        new Thread(PlaylistUpdateSender.getSharedInstance()).start();
    }


    /**
     * This inner class implements the behavior of the "receptionist", whose
     * responsibility is to listen for incoming connection requests. As soon as a
     * new client has arrived, the receptionist delegates the processing to a
     * "servant" who will execute on its own thread.
     */
    private class ReceptionistWorker implements Runnable {

        private boolean isRunning;

        ServerSocket serverSocket;

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


        /**
         * This inner class implements the behavior of the "servants", whose
         * responsibility is to take care of clients once they have connected. This
         * is where we implement the application protocol logic, i.e. where we read
         * data sent by the client and where we generate the responses.
         */
        private class ServantWorker implements Runnable {
            private PrintWriter out;
            private BufferedReader in;

            Socket clientSocket;

            public ServantWorker(Socket clientSocket) {
                try {
                    this.clientSocket = clientSocket;
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream());

                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void run() {
                try {
                    String request = "";

                    // Authentication phase (check if the session is already created for the client connecting
                    while ((request = in.readLine()) != null) {
                        if (request.equals(Protocol.CONNECTION_REQUEST) || request.equals(Protocol.RECONNECTION_REQUEST))
                            break;
                    }

                    // Ask for the client ID
                    send(Protocol.SEND_ID);
                    int id = Integer.parseInt(in.readLine());

                    // check if the session is already stored
                    if (!sm.idAlreadyStored(id)) {
                        sm.storeSession(new Session(id, new Timestamp(System.currentTimeMillis())));
                        send(Protocol.SESSION_CREATED);
                    } else {
                        sm.updateSession(id);
                        send(Protocol.SESSION_UPDATED);
                    }

                    // If the client has already connected once and send has send the RECONNECTION_REQUEST
                    // it means that it wants to send another information
                    if (request.equals(Protocol.RECONNECTION_REQUEST)) {
                        // after the authentication phase, we
                        switch (in.readLine()) {
                            case Protocol.SEND_INFO:
                                String infoReceived = receive();
                                // TODO: transfer the info to the main Controller
                                break;

                            case Protocol.SEND_MUSIC:
                                // Delegate the job to the FileManager
                                FileManager.getInstance().retrieveFile(clientSocket.getInputStream());
                                LOG.info("Music received!");
                                break;

                            default:
                                break;
                        }
                    }

                    LOG.info("Cleaning up resources...");
                    clientSocket.close();
                    out.close();
                    in.close();

                } catch (Exception ex) {
                    // We check if some resources need to be closed
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex1) {
                            LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                        }
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (IOException ex1) {
                            LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                        }
                    }
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

            public void send(String str) {
                out.write(str);
                out.write('\n');
                out.flush();
            }

            public String receive() {
                try {
                    return in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
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

    /**
     * Disconnect the server and its threads.
     */
    public void disconnect() {
        receptionist.stop();
        PlaylistUpdateSender.getSharedInstance().stop();
        ServerDiscovery.getSharedInstance().stop();
        scheduledExecutorService.shutdown();

        LOG.log(Level.INFO, "Server disconnected.");
    }
}
