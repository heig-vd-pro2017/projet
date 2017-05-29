package ch.tofind.commusica;

import ch.tofind.commusica.ui.ClientServerDialog;
import ch.tofind.commusica.utils.Logger;

/**
 * This class is the main class.
 */
public class Commusica {

    private static final Logger LOG = new Logger(Commusica.class.getSimpleName());

    public static void main(String[] args) throws Exception {

        LOG.info("Starting application...");

        ClientServerDialog.launch(ClientServerDialog.class);

        System.exit(0);
    }
}
