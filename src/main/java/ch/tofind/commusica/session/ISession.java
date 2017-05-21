package ch.tofind.commusica.session;

import java.util.Date;

public interface ISession {

    /**
     * @brief Get the session's ID.
     *
     * @return ID of the session.
     */
    abstract Integer getId();

    /**
     * @brief Update the session.
     */
    abstract void update();

    /**
     * @brief Get the session's last update.
     *
     * @return Date when the session was updated.
     */
    abstract Date getLastUpdate();
}
