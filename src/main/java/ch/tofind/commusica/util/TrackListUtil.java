package ch.tofind.commusica.util;

import javafx.scene.media.Media;
import ch.tofind.commusica.media.Track;


import java.io.File;

/**
 * Created by Thibaut-PC on 04.04.17.
 */
public class TrackListUtil {



    public static Media fileToMedia(Track track){

        if(track != null){

          //  File file = new File(track.getUri().);
           // if(!file.isDirectory())

                //String cleanURL = cleanURL(file.getAbsolutePath());

                String uri = track.getUri();
               Media media = new Media(uri);

               return media;


        }

   return null;

    }











    private static String cleanURL(String url) {
        url = url.replace("\\", "/");
        url = url.replaceAll(" ", "%20");
        url = url.replace("[", "%5B");
        url = url.replace("]", "%5D");
        return url;
    }

}
