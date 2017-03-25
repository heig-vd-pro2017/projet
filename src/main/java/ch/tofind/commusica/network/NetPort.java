package ch.tofind.commusica.network;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * For now you a client can connet to the server (by using telnet for example), say its id (for test) and the session
 * will be created or not if the id is already stored
 * The management of obsolete sessions doesn't work well. It only runs one time.
 */

abstract class NetPort {


    final static String SEND_ID = "SEND_ID";
    final static String SESSION_CREATED = "SESSION_CREATED";
    final static String SESSION_UPDATED = "SESSION_UPDATED";

    final static Logger LOG = Logger.getLogger(NetPort.class.getName());
    protected int port = 8080;

    protected PrintWriter out;
    protected  BufferedReader in;


    public NetPort() {
    }

    public NetPort(int port) {
        this.port = port;
    }


    protected void send(String str) {

        out.write(str);
        out.write('\n');
        out.flush();
    }


    protected String receive() {

        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
