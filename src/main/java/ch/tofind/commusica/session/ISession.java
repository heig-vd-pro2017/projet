package ch.tofind.commusica.session;

import java.util.Date;

/**
 * @brief These methods must be defined when implementing a session object.
 */
public interface ISession {

    /**
     * @brief Get the session's ID.
     *
     * @return ID of the session.
     */
    Integer getId();

    /**
     * @brief Update the session.
     */
    void update();

    /**
     * @brief Get the session's last update.
     *
     * @return Date when the session was updated.
     */
    Date getLastUpdate();
}
