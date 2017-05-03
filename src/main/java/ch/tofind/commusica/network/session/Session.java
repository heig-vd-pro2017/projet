package ch.tofind.commusica.network.session;

import java.util.Date;

public class Session {

    //!
    private String id;

    //!
    private Date activeSince;

    public Session(String id) {
        this.id = id;
        this.activeSince = new Date();
    }

    public String getId() {
        return id;
    }

    public Date getActiveSince() {
        return activeSince;
    }

    public void update() {
        activeSince = new Date();
    }
}
