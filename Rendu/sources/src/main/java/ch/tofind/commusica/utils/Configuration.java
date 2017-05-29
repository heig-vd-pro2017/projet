package ch.tofind.commusica.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class can get properties from a configuration file.
 */
public class Configuration {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(Configuration.class.getSimpleName());

    //! The configuration file to use.
    private static final String CONFIG_FILE = "commusica.properties";

    //! Shared instance of the object for all the application.
    private static Configuration instance = null;

    //! Property object to load property from configuration file
    private Properties configuration = null;

    /**
     * Configuration single constructor. Avoid the instantiation.
     */
    private Configuration() {
        configuration = new Properties();

        try {
            configuration.load(new FileInputStream(CONFIG_FILE));
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    /**
     * Get the object instance
     * @return The instance of the object
     */
    public static Configuration getInstance() {

        if(instance == null) {
            synchronized (Configuration.class) {
                if (instance == null) {
                    instance = new Configuration();
                }
            }
        }

        return instance;
    }

    /**
     * Get the property from the configuration file
     * @return The property
     */
    public String get(String property) {
        return configuration.getProperty(property);
    }
}
