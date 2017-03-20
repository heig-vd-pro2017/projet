import java.sql.Timestamp;

/**
 * Created by David on 20.03.2017.
 */
public class Session {

    public Session(int id, Timestamp dateAdded) {
        this.id = id;
        this.dateAdded = dateAdded;
    }

    private int id;
    private Timestamp dateAdded;

    public int getId() {
        return id;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void updateDateAdded() {
        dateAdded = new Timestamp(System.currentTimeMillis());
    }
}
