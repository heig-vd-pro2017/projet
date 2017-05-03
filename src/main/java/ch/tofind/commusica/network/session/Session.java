package ch.tofind.commusica.network.session;

import java.sql.Timestamp;

public class Session {

    //!
    private int id;

    //!
    private Timestamp activeSince;

    //!
    private boolean active;

    public Session(int id, Timestamp dateAdded) {
        this.id = id;
        this.activeSince = dateAdded;
        this.active = true;
    }

    public int getId() {
        return id;
    }

    public Timestamp getActiveSince() {
        return activeSince;
    }

    public boolean getActive(){
        return active;
    }

    public void setActive (boolean state) {
        this.active = state;
    }

    public void setActiveSince(Timestamp timestamp) {
        activeSince = timestamp;
    }
}
