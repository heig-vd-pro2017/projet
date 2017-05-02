package ch.tofind.commusica.network;

import java.sql.Timestamp;

/**
 * Created by David on 20.03.2017.
 */
public class Session {

    public Session(int id, Timestamp dateAdded) {
        this.id = id;
        this.dateAdded = dateAdded;
        this.state = true;
    }

    private int id;
    private Timestamp dateAdded;
    private boolean state;

    public int getId() {
        return id;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public boolean isActive(){
        return state;
    }

    public void setState (boolean state) {
        this.state = state;
    }

    public void updateDateAdded() {
        dateAdded = new Timestamp(System.currentTimeMillis());
    }
}
