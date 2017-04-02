package ch.tofind.commusica.fileManager;

import javax.security.auth.login.Configuration;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * Created by Thibaut-PC on 30.03.17.
 */
public class FileManager {

    private static FileManager instance = null;



    public FileManager(){
    }




    /**
     *
     * @param path
     * @return
     */


    public static long freeSpace(String path){


        long GoFree = 0L;
        try {
                // prints file and directory paths
                File file = new File(path.toString());
                //capacité de la partition
                long totalSpace = file.getTotalSpace();

                //Espace disponible
                long freeSpace = file.getFreeSpace();


                long Go = totalSpace / (1024 * 1024 * 1024);

                 GoFree = freeSpace ;

        } catch(Exception e) {

            // if any error occurs
            e.printStackTrace();
        }

        return GoFree;
    }





    /**
     *
     * @param path
     * @param fileName
     * @param file
     * @return
     * @throws Exception
     * @throws FileNotFoundException
     */


public boolean save(String path ,String fileName, File file) throws Exception, FileNotFoundException {

        String pathFile = path.toString() + File.separator + fileName;
    File infile = null;
    File oFile  = null;

    if(file.length() > freeSpace(path)){

            throw new Exception("no free space");
        }
    file = new File(file.getPath());
    oFile = new File(pathFile);

    FileInputStream is = new FileInputStream(file);
    FileOutputStream fos = new FileOutputStream(oFile);
    FileChannel f = is.getChannel();
    FileChannel f2 = fos.getChannel();

        try{

            ByteBuffer buf = ByteBuffer.allocateDirect(64 * 1024);
            long len = 0;
            while((len = f.read(buf)) != -1) {
                buf.flip();
                f2.write(buf);
                buf.clear();
            }
            f.close();
            f2.close();

        }
        catch (IOException ex) {
            System.out.println("Oops Unable to proceed file write Operation due to ->" + ex.getMessage());
        } finally {
            try {
                f.close();
                f2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    return true;
   }






    /**
     *
      * @param filename
     * @return
     * @throws Exception
     */


public boolean eraseFile(String path, String filename)  throws Exception{

    File file = new File(path + File.separator+ filename);

    System.out.println(path + filename);
    // Check if the file exists

    if(!file.exists()) {

        throw new Exception("file not found");
    }

    //Test properties and rights to the file

    if(!file.canWrite()){

        throw new Exception( "Insufficient right to access file");
    }

return file.delete();
}


    public static FileManager getInstance() {

        if(instance == null) {
            synchronized (FileManager.class) {
                if (instance == null) {
                    instance = new FileManager();
                }
            }
        }

        return instance;
    }

}






