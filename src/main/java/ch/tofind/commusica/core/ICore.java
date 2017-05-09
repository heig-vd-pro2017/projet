package ch.tofind.commusica.core;

import java.net.InetAddress;

public interface ICore {

    abstract void sendUnicast(InetAddress hostname, String message);

    abstract void sendMulticast(String message);

    abstract void stop();
}
