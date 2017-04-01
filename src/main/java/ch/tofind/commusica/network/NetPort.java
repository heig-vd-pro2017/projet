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
 * SEE IF STILL NEEDED
 *
 * Interface used for the abstraction of the client/server over a socket and provide simple send and receive methods
 *
 */

interface NetPort {


    String SEND_ID = "SEND_ID";
    String SESSION_CREATED = "SESSION_CREATED";
    String SESSION_UPDATED = "SESSION_UPDATED";

    void send(String str);
    String receive();

}
