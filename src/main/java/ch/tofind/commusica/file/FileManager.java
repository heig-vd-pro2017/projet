package ch.tofind.commusica.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

public class FileManager {

    public static final byte[] MP3 = {0x49, 0x44, 0x33};
    public static final int OFFSET_MP3_SIGNATURE = 0;
    public static final byte[] M4A = {0x66, 0x74, 0x79, 0x70, 0x4D, 0x34, 0x41, 0x20};
    public static final int OFFSET_M4A_SIGNATURE = 4;
    public static final byte[] WAV = {0x57, 0x41, 0x56, 0x45, 0x66, 0x6D, 0x74, 0x20};
    public static final int OFFSET_WAV_SIGNATURE = 8;


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
        File result = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            byte[] receivedMusic = new byte[8192];
            // TODO: change the path/name corresponding to our specs
            result = new File("." + File.separator + "tracks" + File.separator + UUID.randomUUID() + ".mp3");
            fos = new FileOutputStream(result);
            bos = new BufferedOutputStream(fos);

            int bytesRead = 0;

            byte[] signature = new byte[16];

            is.read(signature, 0, 16);

            if (!signatureChecker(signature)) {
                System.out.println("File not compatible!");
                Files.delete(result.toPath());
            } else {
                bos.write(signature, 0, signature.length);
                while ((bytesRead = is.read(receivedMusic)) != -1) {
                    bos.write(receivedMusic, 0, bytesRead);
                }
                bos.flush();
                // TODO: save it with good name and a good path
            }
            fos.close();
            bos.close();
            is.close();

        } catch (IOException e) {
            if (result != null)
                result.delete();

            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            if (bos != null)
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            if (is != null)
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            e.printStackTrace();
        }
    }

    public static boolean signatureChecker(byte[] signature) {
        if ((Arrays.equals(MP3, Arrays.copyOfRange(signature, OFFSET_MP3_SIGNATURE, MP3.length))) ||
                Arrays.equals(M4A, Arrays.copyOfRange(signature, OFFSET_M4A_SIGNATURE, M4A.length)) ||
                Arrays.equals(WAV, Arrays.copyOfRange(signature, OFFSET_WAV_SIGNATURE, WAV.length))) {
            return true;
        }
        return false;
    }

    /**
     * @param filePath
     * @return
     */
    public boolean delete(String filePath) {
        return new File(filePath).delete();
    }
}






