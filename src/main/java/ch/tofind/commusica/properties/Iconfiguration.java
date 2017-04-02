package ch.tofind.commusica.properties;

import java.io.IOException;

/**
 * Created by Thibaut-PC on 31.03.17.
 */
public interface Iconfiguration {

    public int getServerport();
    public void loadPropertie(String fileName) throws IOException;
    public String getServerAddress();
    public String getNamedefualtplaylist();
    public String getNameDatabase();
    public String getPath();

}
