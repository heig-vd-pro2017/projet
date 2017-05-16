package ch.tofind.commusica.file;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import ch.tofind.commusica.utils.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;

/**
 * @brief This class represents the file manager and allows interaction with the filesystem.
 */
public class FileManager {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(FileManager.class.getSimpleName());

    /**
     * These constants are used to check the format of a file.
     * The arrays are the sequences of byte contained in each file of the specified format.
     * The OFFSETs are the offset of the signature in the file (for example the sequence of bytes 0x49, 0x44, 0x33 are
     * the first 3 bytes of an MP3 file).
     */
    public static final byte[] MP3 = {0x49, 0x44, 0x33};
    public static final int OFFSET_MP3_SIGNATURE = 0;
    public static final byte[] M4A = {0x66, 0x74, 0x79, 0x70, 0x4D, 0x34, 0x41, 0x20};
    public static final int OFFSET_M4A_SIGNATURE = 4;
    public static final byte[] WAV = {0x57, 0x41, 0x56, 0x45, 0x66, 0x6D, 0x74, 0x20};
    public static final int OFFSET_WAV_SIGNATURE = 8;


    private static FileManager instance = null;

    private FileManager() {

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
     * @param path
     * @param fileName
     * @param file
     */
    public void save(File file, String path, String fileName) {
        file.renameTo(new File(path + File.separator + fileName));
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
            if(!result.renameTo(newName)) {
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
        if (Arrays.equals(MP3, Arrays.copyOfRange(signature, OFFSET_MP3_SIGNATURE, OFFSET_MP3_SIGNATURE + MP3.length))) {
            return ".mp3";
        } else if (Arrays.equals(M4A, Arrays.copyOfRange(signature, OFFSET_M4A_SIGNATURE, OFFSET_M4A_SIGNATURE + M4A.length))) {
            return ".m4a";
        } else if (Arrays.equals(WAV, Arrays.copyOfRange(signature, OFFSET_WAV_SIGNATURE, OFFSET_WAV_SIGNATURE + WAV.length))) {
            return ".wav";
        }
        return "error";
    }


    /**
     * Get the first nbByte of a File.
     * @param file the file itself
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
     * @param file the file to check
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






