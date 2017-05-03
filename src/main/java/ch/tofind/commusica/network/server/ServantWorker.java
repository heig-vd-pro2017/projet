package ch.tofind.commusica.network.server;

import ch.tofind.commusica.file.FileManager;
import ch.tofind.commusica.network.Protocol;
import ch.tofind.commusica.network.session.Session;
import ch.tofind.commusica.network.session.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This inner class implements the behavior of the "servants", whose
 * responsibility is to take care of clients once they have connected. This
 * is where we implement the application protocol logic, i.e. where we read
 * data sent by the client and where we generate the responses.
 */
public class ServantWorker implements Runnable {

    //! Logger for debugging proposes.
    final static Logger LOG = Logger.getLogger(ServantWorker.class.getName());

    //!
    private SessionManager sessionManager = SessionManager.getInstance();

    //!
    private PrintWriter out;

    //!
    private BufferedReader in;

    //!
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


            sessionManager.storeSession(new Session(id, new Timestamp(System.currentTimeMillis())));
            send(Protocol.SESSION_STORED);


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
