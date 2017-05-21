package ch.tofind.commusica.session;

import java.net.InetAddress;
import java.util.Date;

/**
 * @brief Class representing a server session.
 */
public class ServerSession implements ISession {

    //! ID of the session.
    private Integer id;

    //! IP address of the server
    private InetAddress serverIp;

    //! Name of the server
    private String serverName;

    //! Last time the session was updated.
    private Date updated;

    /**
     * @brief Create a session.
     *
     * @param id ID of the session.
     * @param serverIp IP of the server.
     * @param serverName Name of the server.
     */
    public ServerSession(Integer id, InetAddress serverIp, String serverName) {
        this.id = id;
        this.serverIp = serverIp;
        this.serverName = serverName;
        this.updated = new Date();
    }

    /**
     * @brief Get the server's IP associated with the session.
     *
     * @return The server's IP.
     */
    public InetAddress getServerIp() {
        return serverIp;
    }

    /**
     * @brief Get the server's name associated with the session.
     *
     * @return The server's name.
     */
    public String getServerName() {
        return serverName;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void update() {
        updated = new Date();
    }

    @Override
    public Date getLastUpdate() {
        return updated;
    }

    @Override
    public String toString() {
        return "Nom: " + serverName + " " + serverIp;
    }

}
