package ch.tofind.commusica.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Thibaut-PC on 31.03.17.
 */
public class Configuration implements Iconfiguration {


    private int portServer;
    private String nameDatabase;
    private String namedefaultplaylist;
    private String path;

    public Configuration() throws IOException {

        loadPropertie("configuration.");

    }

    @Override
    /**
     *
     */
    public int getServerport() {
        return portServer;
    }

    @Override
    public void loadPropertie(String fileName) throws IOException {
        FileInputStream f = new FileInputStream(fileName);
        Properties properpties = new Properties();
        properpties.load(f);

        this.path = properpties.getProperty("path");
        this.portServer = Integer.parseInt(properpties.getProperty("Serverport"));
        this.nameDatabase = properpties.getProperty("nameDatabase");
    }

    @Override
    public String getServerAddress() {

        return null;
    }

    @Override
    public String getNamedefualtplaylist() {

        return namedefaultplaylist;
    }

    @Override
    public String getNameDatabase() {

        return nameDatabase;
    }

    @Override
    public String getPath() {
        return path;
    }
}
