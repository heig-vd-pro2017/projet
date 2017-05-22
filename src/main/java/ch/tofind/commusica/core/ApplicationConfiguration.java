package ch.tofind.commusica.core;

import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.network.NetworkProtocol;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * @brief This class is only used to set the application configuration.
 */
public class ApplicationConfiguration {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(ApplicationConfiguration.class.getSimpleName());

    /**
     * @brief Configure the application.
     */
    public static void configure() {

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
}