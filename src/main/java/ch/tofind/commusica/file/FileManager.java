package ch.tofind.commusica.file;

import java.io.*;
import java.nio.file.Files;
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
 * Singleton class used to manage the actions on the files.
 * It is the class which will retrieve the file over a TCP socket.
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
            result = new File("." + File.separator + "tracks" + File.separator + result);
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

            result.renameTo(new File("." + File.separator + "tracks" + File.separator + UUID.randomUUID() + ext));
            return result.getAbsolutePath();

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

    // Used for tests only
    public static void displayMetadatas(File f) {
        try {
            AudioFile af = AudioFileIO.read(f);


            // System.out.println(af.getAudioHeader().getTrackLength() / 60 + ":" + af.getAudioHeader().getTrackLength() % 60);

            System.out.println(af.getAudioHeader().getFormat());
            System.out.println(af.getTag().getFirst(FieldKey.ARTIST));
            System.out.println(af.getTag().getFirst(FieldKey.ALBUM));
            System.out.println(af.getTag().getFirst(FieldKey.TITLE));
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
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






