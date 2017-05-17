package ch.tofind.commusica.file;

import ch.tofind.commusica.utils.Logger;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @brief This class represents the file manager and allows interaction with the filesystem.
 */
public class FileManager {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(FileManager.class.getSimpleName());

    //! Instance of the object shared for all the application
    private static FileManager instance = null;

    private FileManager() {

        // create the tracks directory which will contain the tracks
        File tracksDir = new File("." + File.separator + "tracks");

        tracksDir.mkdir();
    }

    /**
     * Returns the Singleton of the FileManager
     *
     * @return the instance of the FileManager
     */
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
     * Retrive a file form a TCP socket. It also check the format of the file.
     * For now it accept MP3, M4A and WAV.
     *
     * @param is       InputStream of the socket
     * @param fileSize size of the file
     * @return the path where the file is stored as a String
     */
    public String retrieveFile(InputStream is, int fileSize) {
        File result = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            byte[] receivedMusic = new byte[8192];
            //result = new File("." + File.separator + "tracks" + File.separator + UUID.randomUUID() + ".mp3");
            result = new File("." + File.separator + "tracks" + File.separator + "tmp");
            fos = new FileOutputStream(result);
            bos = new BufferedOutputStream(fos);

            // file extension
            String ext;

            // variables used to control the transfer
            int bytesRead;
            int remaining = fileSize;

            // We check the 16 first bytes so we can check if it is a compatible file type
            byte[] signature = new byte[16];
            is.read(signature, 0, 16);
            ext = signatureChecker(signature);

            if (ext.equals("error")) {
                System.out.println("File not compatible!");
                fos.close();
                bos.close();
                Files.delete(result.toPath());
            } else {
                bos.write(signature, 0, signature.length);
                remaining -= signature.length;
                while ((bytesRead = is.read(receivedMusic, 0, Math.min(remaining, receivedMusic.length))) > 0) {
                    remaining -= bytesRead;
                    bos.write(receivedMusic, 0, bytesRead);
                }
                System.out.println("Music received!");
                bos.flush();
                fos.close();
                bos.close();
            }


            String MD5 = getMD5(result);
            File newName = new File("." + File.separator + "tracks" + File.separator + MD5 + ext);

            // if the renaming failed (name already in the directory)
            if (!result.renameTo(newName)) {
                Files.delete(result.toPath());
            }

            return newName.getPath();

        } catch (IOException e) {
            // we delete the file if an problem occurred
            if (result != null)
                try {
                    Files.delete(result.toPath());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

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
        return null;
    }


    /**
     * Check if the File passed as parameter is of a supported format. If so, it return a String corresponding to the
     * extension (ex.: .mp3 if it is a MP3)
     *
     * @param signature
     * @return a String of the extension corresponding to the file format.
     */
    public static String signatureChecker(byte[] signature) {
        if (Arrays.equals(AudioFilesOffset.MP3, Arrays.copyOfRange(signature, AudioFilesOffset.OFFSET_MP3_SIGNATURE, AudioFilesOffset.OFFSET_MP3_SIGNATURE + AudioFilesOffset.MP3.length))) {
            return ".mp3";
        } else if (Arrays.equals(AudioFilesOffset.M4A, Arrays.copyOfRange(signature, AudioFilesOffset.OFFSET_M4A_SIGNATURE, AudioFilesOffset.OFFSET_M4A_SIGNATURE + AudioFilesOffset.M4A.length))) {
            return ".m4a";
        } else if (Arrays.equals(AudioFilesOffset.WAV, Arrays.copyOfRange(signature, AudioFilesOffset.OFFSET_WAV_SIGNATURE, AudioFilesOffset.OFFSET_WAV_SIGNATURE + AudioFilesOffset.WAV.length))) {
            return ".wav";
        }
        return "error";
    }

    /**
     * Get the first nbByte of a File.
     *
     * @param file    the file itself
     * @param nbBytes number of bytes wanted
     * @return a byte array containing the nbBytes first bytes of the file
     */
    public static byte[] getFirstBytes(File file, int nbBytes) {

        // check if the file is too small
        if (file.length() < nbBytes) {
            return null;
        }

        byte[] result = new byte[nbBytes];
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedInputStream fileBytes = new BufferedInputStream(fis);

        try {
            fileBytes.read(result, 0, nbBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileBytes.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @param filePath
     * @return
     */
    public boolean delete(String filePath) {
        return new File(filePath).delete();
    }


    /**
     * Return a String of the checksum for the specified file with the specified digest
     * taken from the here: http://howtodoinjava.com/core-java/io/how-to-generate-sha-or-md5-file-checksum-hash-in-java/
     *
     * @param digest
     * @param file
     * @return a string of the checksum
     * @throws IOException
     */
    public static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }


        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    /**
     * Check if the MD5 checksum is the same as the MD5 passed in param
     *
     * @param file        the file to check
     * @param MD5checksum the MD5 to compare
     * @return true if the checksum are the same, false if they are different
     * @throws IOException
     */
    public static boolean checkFileMD5(File file, String MD5checksum) throws IOException {
        //Use MD5 algorithm
        MessageDigest md5Digest = null;
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return getFileChecksum(md5Digest, file).equals(MD5checksum);
    }

    public static String getMD5(File file) throws IOException {
        MessageDigest md5Digest = null;
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return getFileChecksum(md5Digest, file);
    }
}






