package ch.tofind.commusica.ui;

import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.Network;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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
                Core.setupAsServer();
            }
            else {
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
}
