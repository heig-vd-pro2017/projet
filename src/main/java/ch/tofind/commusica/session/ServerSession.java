package ch.tofind.commusica.session;

import java.net.InetAddress;
import java.util.Date;


/**
 * Class representing a server session with an InetAddress, a name and an id. It also store a Date representing
 * the last time the session was updated.
 *
 * TODO: CLEAN THE CLASS
 */
public class ServerSession {

    //!
    private InetAddress ip;

    //!
    private String name;

    //!
    private int id;

    //!
    private Date updated;

    public ServerSession(InetAddress ip, String name, int id) {
        this.ip = ip;
        this.name = name;
        this.updated = new Date();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void update() {
        updated = new Date();
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String toString() {
        return name + " " + ip;
    }


}
