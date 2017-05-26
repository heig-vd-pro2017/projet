package ch.tofind.commusica;

import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.ui.ClientServerDialog;
import ch.tofind.commusica.ui.UIController;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.Network;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.util.Optional;

public class Commusica {

    private static final Logger LOG = new Logger(Commusica.class.getSimpleName());

    public static void main(String[] args) throws Exception {

        LOG.info("Starting application...");

        ClientServerDialog.launch(ClientServerDialog.class);

        System.exit(0);
    }

}
