package ch.tofind.commusica.network.server;

import ch.tofind.commusica.Commusica;
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
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the behavior of the "servants", whose
 * responsibility is to take care of clients once they have connected. This
 * is where we implement the application protocol logic, i.e. where we read
 * data sent by the client and where we generate the responses.
 */
public class ServantWorker implements Runnable {

    //!
    private SessionManager sessionManager = SessionManager.getInstance();

    //!
    private Socket socket;

    //!
    private PrintWriter out;

    //!
    private BufferedReader in;


    public ServantWorker(Socket socket) {

        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        // Récupère ce qui a été envoyé par le client
        ArrayList<String> commands = new ArrayList<>();

        try {

            String input = in.readLine();

            while (input != null) {
                commands.add(input);
                input = in.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Récupère la commande
        String command = commands.remove(0);

        // Récupère l'ID de l'utilisateur ayant effectué la commande
        String user = commands.remove(1);

        // Vérifie si l'utilisateur a déjà une session ou non et la crée au besoin
        SessionManager.getInstance().store(user);

        // Prépare les arguments à envoyer au controlleur
        ArrayList<Object> args = new ArrayList<>();

        // Ajoute les éventuels arguments pour des commandes spécifiques
        switch (command) {
            case Protocol.SEND_MUSIC:
                args.add(socket);
        }

        // Ajoute le reste des arguments
        args.addAll(commands);

        // Envoie la commande demandée au contrôleur et en récupère le résultat
        String result = Commusica.execute(command, args);

        // Renvoie le résultat au client
        out.write(result + '\n');

        // Ferme la connection
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.close();

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
