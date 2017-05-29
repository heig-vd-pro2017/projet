package ch.tofind.commusica.session;

import java.util.Date;

/**
 * These methods must be defined when implementing a session object.
 */
public interface ISession {

    /**
     * Get the session's ID.
     *
     * @return ID of the session.
     */
    Integer getId();

    /**
     * Update the session.
     */
    void update();

    /**
     * Get the session's last update.
     *
     * @return Date when the session was updated.
     */
    Date getLastUpdate();
}
