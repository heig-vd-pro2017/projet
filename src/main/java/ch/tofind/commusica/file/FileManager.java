package ch.tofind.commusica.file;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;

import java.io.*;
import java.nio.file.Files;
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

    public File retrieveFile(InputStream is) {
        File result = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            byte[] receivedMusic = new byte[8192];
            result = new File("." + File.separator + "tracks" + File.separator + UUID.randomUUID() + ".mp3");
            fos = new FileOutputStream(result);
            bos = new BufferedOutputStream(fos);

            int bytesRead = 0;

            // We check the 16 first bytes so we can check if it is a compatible file type
            byte[] signature = new byte[16];
            is.read(signature, 0, 16);

            if (!signatureChecker(signature)) {
                System.out.println("File not compatible!");
                fos.close();
                bos.close();
                Files.delete(result.toPath());
            } else {
                bos.write(signature, 0, signature.length);
                while ((bytesRead = is.read(receivedMusic)) != -1) {
                    bos.write(receivedMusic, 0, bytesRead);
                }
                System.out.println("Music received!");
                bos.flush();
                fos.close();
                bos.close();
            }
            //is.close();

            return result;

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

    public static boolean signatureChecker(byte[] signature) {
        if ((Arrays.equals(MP3, Arrays.copyOfRange(signature, OFFSET_MP3_SIGNATURE, MP3.length))) ||
                Arrays.equals(M4A, Arrays.copyOfRange(signature, OFFSET_M4A_SIGNATURE, M4A.length)) ||
                Arrays.equals(WAV, Arrays.copyOfRange(signature, OFFSET_WAV_SIGNATURE, WAV.length))) {
            return true;
        }
        return false;
    }

     public static void displayMetadatas(File f) {
         try {
             AudioFile af = AudioFileIO.read(f);

             System.out.println("toString: " + af.toString());
             System.out.println("getFile: " + af.getFile());
             System.out.println("getBaseFileName: " + af.getBaseFilename(f));
/*
             System.out.println(af.getAudioHeader().getTrackLength() / 60 + ":" + af.getAudioHeader().getTrackLength() % 60);
             System.out.println(af.getAudioHeader().getFormat());
             System.out.println(af.getTag().getFirst(FieldKey.ARTIST));
             System.out.println(af.getTag().getFirst(FieldKey.ALBUM));
             System.out.println(af.getTag().getFirst(FieldKey.TITLE));*/
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






