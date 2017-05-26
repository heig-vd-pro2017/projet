package ch.tofind.commusica.session;

import ch.tofind.commusica.utils.Configuration;

/**
 * @brief These methods must be defined when implementing a session manager object.
 */
public interface ISessionManager {

    //! Time before a session is considered inactive.
    int TIME_BEFORE_SESSION_INACTIVE = Integer.valueOf(Configuration.getInstance().get("TIME_BEFORE_SESSION_INACTIVE"));

    /**
     * @brief Stops the session manager.
     */
    void stop();

}
