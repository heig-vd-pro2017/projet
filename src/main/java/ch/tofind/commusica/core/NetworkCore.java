package ch.tofind.commusica.core;

import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.network.NetworkProtocol;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * @brief This core is only used to set the network properties for the rest of the application.
 */
public class NetworkCore extends AbstractCore implements ICore {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(NetworkCore.class.getSimpleName());

    /**
     * @brief Setup the core as a network core.
     */
    public NetworkCore() {

        Scanner scanner = new Scanner(System.in);

        TreeMap<String, InetAddress> networkInterfaces = ch.tofind.commusica.utils.Network.getIPv4Interfaces();

        if (networkInterfaces.size() > 1) {

            LOG.info("Choosing network interface.");

            String interfaceChoice = "";
            while (!networkInterfaces.containsKey(interfaceChoice)) {
                System.out.println("Which interface to use for the multicast ?");
                for (Map.Entry<String, InetAddress> networkInterface : networkInterfaces.entrySet()) {
                    System.out.println(networkInterface.getKey() + " - " + networkInterface.getValue());
                }
                System.out.print("> ");
                interfaceChoice = scanner.next();
            }

            NetworkProtocol.interfaceToUse = networkInterfaces.get(interfaceChoice);
        } else {

            LOG.info("Network interface has been chosen automatically.");

            NetworkProtocol.interfaceToUse = networkInterfaces.firstEntry().getValue();
        }

        ApplicationProtocol.myId = Arrays.hashCode(ch.tofind.commusica.utils.Network.getMacAddress(NetworkProtocol.interfaceToUse));
    }

    @Override
    public void sendUnicast(InetAddress hostname, String message) {
        // Do nothing
    }

    @Override
    public void sendMulticast(String message) {
        // Do nothing
    }

    @Override
    public void stop() {
        // Do nothing
    }
}