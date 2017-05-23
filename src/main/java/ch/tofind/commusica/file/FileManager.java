package ch.tofind.commusica.file;

import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.utils.Logger;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

/**
 * @brief This class represents the file manager and allows interaction with the filesystem.
 */
public class FileManager {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(FileManager.class.getSimpleName());

    //! Instance of the object shared for all the application.
    private static FileManager instance = null;

    //! Output of the saved tracks.
    public static String OUTPUT_DIRECTORY = Configuration.getInstance().get("DEFAULT_TRACKS_DIRECTORY");

    /**
     * @brief FileManager single constructor. Avoid the instantiation.
     */
    private FileManager() {

        File outputDirectory = new File(OUTPUT_DIRECTORY);

        outputDirectory.mkdir();
    }

    /**
     * @brief Get the object instance.
     *
     * @return The instance of the object.
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
     * @brief Retrieve a file.
     *
     * @param inputStream The origin of the file.
     * @param fileSize Size of the file.
     *
     * @return The stored file.
     */
    public File retrieveFile(InputStream inputStream, int fileSize) {

        byte[] networkPacketSize = new byte[8192];

        String tmpFileName = UUID.randomUUID().toString();

        File tmpFile = new File(OUTPUT_DIRECTORY + File.separator + tmpFileName);

        BufferedOutputStream bufferedFileStream;

        FileOutputStream fileStream = null;
        try {
            fileStream = new FileOutputStream(tmpFile);
        } catch (FileNotFoundException e) {
            LOG.error(e);
        }

        bufferedFileStream = new BufferedOutputStream(fileStream);

        int bytesRead;

        int remainingBytes = fileSize;

        try {

            while ((bytesRead = inputStream.read(networkPacketSize, 0, Math.min(remainingBytes, networkPacketSize.length))) > 0) {
                bufferedFileStream.write(networkPacketSize, 0, bytesRead);
                remainingBytes -= bytesRead;
            }

        } catch (IOException e) {

            delete(tmpFile);

            LOG.error(e);

            return null;
        }

        try {
            bufferedFileStream.flush();
        } catch (IOException e) {
            LOG.error(e);
        }

        try {
            fileStream.close();
        } catch (IOException e) {
            LOG.error(e);
        }

        try {
            bufferedFileStream.close();
        } catch (IOException e) {
            LOG.error(e);
        }

        return tmpFile;
    }

    /**
     * @brief Check if the file is supported.
     *
     * @param file The file to check.
     *
     * @return The file's extension corresponding to the file format.
     */
    public String getFormatExtension(File file) throws Exception {

        InputStream inputStream = new FileInputStream(file);

        byte[] fileHeader = getBytes(file, 16);

        inputStream.close();

        if (Arrays.equals(FilesFormats.MP3_HEADER, Arrays.copyOfRange(fileHeader, FilesFormats.MP3_HEADER_OFFSET, FilesFormats.MP3_HEADER_OFFSET + FilesFormats.MP3_HEADER.length))) {
            return FilesFormats.MP3_EXTENSION;
        } else if (Arrays.equals(FilesFormats.M4A_HEADER, Arrays.copyOfRange(fileHeader, FilesFormats.M4A_HEADER_OFFSET, FilesFormats.M4A_HEADER_OFFSET + FilesFormats.M4A_HEADER.length))) {
            return FilesFormats.M4A_EXTENSION;
        } else if (Arrays.equals(FilesFormats.WAV_HEADER, Arrays.copyOfRange(fileHeader, FilesFormats.WAV_HEADER_OFFSET, FilesFormats.WAV_HEADER_OFFSET + FilesFormats.WAV_HEADER.length))) {
            return FilesFormats.WAV_EXTENSION;
        } else {
            throw new Exception("File not supported.");
        }
    }

    /**
     * @brief Get the Nth first bytes from a file.
     *
     * @param file The file.
     * @param nbBytes Number of bytes wanted.
     *
     * @return The Nth first bytes from the file.
     */
    public byte[] getBytes(File file, int nbBytes) {

        if (file.length() < nbBytes) {
            return null;
        }

        byte[] nFirstBytes = new byte[nbBytes];

        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            LOG.error(e);
        }

        BufferedInputStream fileBytes = new BufferedInputStream(fileStream);

        try {
            fileBytes.read(nFirstBytes, 0, nbBytes);
        } catch (IOException e) {
            LOG.error(e);
        }

        try {
            fileStream.close();
        } catch (IOException e) {
            LOG.error(e);
        }

        try {
            fileBytes.close();
        } catch (IOException e) {
            LOG.error(e);
        }

        return nFirstBytes;
    }

    /**
     * @brief Delete the file from the filesystem.
     *
     * @param file The file to delete.
     *
     * @return Status of the deletion.
     */
    public boolean delete(File file) {
        return file.delete();
    }

    /**
     * @brief Rename the file to a new filename.
     *
     * @param file The file to rename.
     * @param newFilename The new name of the file.
     */
    public void rename(File file, File newFilename) {
        file.renameTo(newFilename);
    }

    /**
     * @brief Get the checksum from the file.
     *
     * @param file The file to get the checksum.
     *
     * @return The checksum of the file.
     */
    public String getMD5Checksum(File file) {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e);
        }

        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            LOG.error(e);
        }

        byte[] buffer = new byte[1024];
        int bytesCount = 0;

        try {
            while ((bytesCount = fileStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesCount);
            }
        } catch (IOException e) {
            LOG.error(e);
        }

        try {
            fileStream.close();
        } catch (IOException e) {
            LOG.error(e);
        }

        byte[] bytes = digest.digest();

        // Convert the array of bytes to hexadecimal format
        StringBuilder hash = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {

            hash.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));

        }

        return hash.toString();
    }
}