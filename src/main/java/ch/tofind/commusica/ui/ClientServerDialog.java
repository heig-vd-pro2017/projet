package ch.tofind.commusica.ui;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.Network;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Optional;

public class ClientServerDialog extends Application {
    private static final Logger LOG = new Logger(ClientServerDialog.class.getSimpleName());

    @Override
    public void start(Stage stage) throws Exception {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setContentText("Welcome to Commusica!\n\nWould you like to be the server?");
        dialog.setHeaderText(null);
        dialog.setTitle("Welcome!");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        dialog.getDialogPane().getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent()) {
            Network.configureNetwork();

            if (result.get() == yesButton) {
                LOG.info("Launching as server.");

                ServerNameChooser serverNameChooser = new ServerNameChooser();
                String serverName = serverNameChooser.askForServername();

                // if the user canceled his choice we exit the app
                if (serverName.equals("")) {
                    Platform.exit();
                }

                // setup the server name
                ApplicationProtocol.serverName = serverName;
                Core.setupAsServer();

            } else {
                LOG.info("Launching as client.");
                Core.setupAsClient();
                
            }
        } else {

            LOG.error("Quitting application because user didn't choose an option.");

            Platform.exit();
        }

        UIController controller = new UIController();

        controller.start(stage);
    }

    private class ServerNameChooser {

        public String askForServername() {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setContentText("Please enter your server name:");
            Optional<String> result;

            // While the user don't enter a text
            do {
                result = dialog.showAndWait();
                if (!result.isPresent()) {
                    return "";
                }
            } while (result.get().equals(""));

            return result.get();
        }

    }
}
