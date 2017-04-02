package ch.tofind.commusica.network;

import ch.tofind.commusica.fileManager.FileManager;
import ch.tofind.commusica.properties.Iconfiguration;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

class Test {
    public static void main(String... args) throws Exception {

        Iconfiguration conf = new ch.tofind.commusica.properties.Configuration();

       String path = conf.getPath();

        File file1 = new File("configuration.");

        Path chemin = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
        File file = new File(chemin +File.separator + path);
       // file.mkdir();

       // FileManager.getInstance().eraseFile(chemin +File.separator + path, "234");
        System.out.println();
        System.out.print(conf.getServerport());
      //  NetworkManager nm = new NetworkManager(8080);
        //nm.serveClients();
    }
}
