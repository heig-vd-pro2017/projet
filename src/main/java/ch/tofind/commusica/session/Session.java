package ch.tofind.commusica.session;

import java.util.Date;

public class Session {

    //! ID of the session
    private Integer id;

    //! Last time the session was updated
    private Date updated;

    /**
     * @brief Create a session
     * @param id ID of the session
     */
    public Session(Integer id) {
        this.id = id;
        this.updated = new Date();
    }

    /**
     * @brief Get the session's ID
     * @return ID of the session
     */
    public Integer getId() {
        return id;
    }

    /**
     * @brief Get the session's last update
     * @return Date when the session was updated
     */
    public Date getUpdate() {
        return updated;
    }

    /**
     * @brief Update the session
     */
    public void update() {
        updated = new Date();
    }
}
