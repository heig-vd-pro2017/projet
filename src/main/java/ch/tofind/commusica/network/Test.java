package ch.tofind.commusica.network;

import ch.tofind.commusica.fileManager.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

class Test {
    public static void main(String... args) throws Exception {
        FileInputStream f = new FileInputStream("configuration.");
        File file1 = new File("configuration.");
        Properties properpties = new Properties();
        properpties.load(f);

String path = properpties.getProperty("path");

        Path chemin = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
        File file = new File(chemin +File.separator + path);
       // file.mkdir();

       // FileManager.save(chemin +File.separator + path, "234", file1);
      System.out.println(FileManager.eraseFile(chemin +File.separator + path, "234"));

        System.out.print(chemin + path);
      //  NetworkManager nm = new NetworkManager(8080);
        //nm.serveClients();
    }
}
