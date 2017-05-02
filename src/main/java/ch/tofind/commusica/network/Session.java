package ch.tofind.commusica.network;

import java.sql.Timestamp;

/**
 * Created by David on 20.03.2017.
 */
public class Session {

    public Session(int id, Timestamp dateAdded) {
        this.id = id;
        this.activeSince = dateAdded;
        this.active = true;
    }

    private int id;
    private Timestamp activeSince;
    private boolean active;

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
