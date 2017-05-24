package ch.tofind.commusica;

import ch.tofind.commusica.ui.UIController;
import ch.tofind.commusica.utils.Logger;

public class Commusica {

    private static final Logger LOG = new Logger(Commusica.class.getSimpleName());

    public static void main(String[] args) throws Exception {

        LOG.info("Starting application...");

        UIController.launch(UIController.class);

    }
}
