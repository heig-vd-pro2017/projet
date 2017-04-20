package ch.tofind.commusica.file;

import java.io.File;

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

    /**
     * @param filePath
     * @return
     */
    public boolean delete(String filePath) {
        return new File(filePath).delete();
    }
}






