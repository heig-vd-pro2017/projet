package ch.tofind.commusica.file;

import java.io.*;

public class FileManager {

    private static FileManager instance = null;

    private FileManager() {

    }

    public static FileManager getInstance() {

        if (instance == null) {
            synchronized (FileManager.class) {
                if (instance == null) {
                    instance = new FileManager();
                }
            }
        }

        return instance;
    }

    /**
     * @param path
     * @param fileName
     * @param file
     */
    public void save(File file, String path, String fileName) {
        file.renameTo(new File(path + File.separator + fileName));
    }

    public void retrieveFile(InputStream is) {
        try {
            byte[] receivedMusic = new byte[8192];
            // TODO: change the path/name corresponding to our specs
            File result = new File("C:\\Users\\David\\Documents\\Test\\test.mp3");
            FileOutputStream fos = new FileOutputStream(result);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            int bytesRead = 0;

            while ((bytesRead = is.read(receivedMusic)) != -1) {
                bos.write(receivedMusic, 0, bytesRead);
            }
            bos.flush();
            // TODO: save it with good name and a good path
            // save(result, aPathToDefine, aNameToDefine);
            fos.close();
            bos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param filePath
     * @return
     */
    public boolean delete(String filePath) {
        return new File(filePath).delete();
    }
}






