package ch.tofind.commusica.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @brief This class can get properties from a configuration file
 */
public class Configuration {

    //! Shared instance of the object for all the application
    private static Configuration instance = null;

    //! Property object to load property from configuration file
    private Properties configuration = null;

    /**
     * @brief Configuration single constructor. Avoid the instantiation.
     */
    private Configuration() {
        configuration = new Properties();
    }

    /**
     * @brief Get the object instance
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
     * @brief Load the configuration file to use
     */
    public void load(String fileName) throws IOException {
        configuration.load(new FileInputStream(fileName));
    }

    /**
     * @brief Get the property from the configuration file
     * @return The property
     */
    public String get(String property) {
        return configuration.getProperty(property);
    }
}
