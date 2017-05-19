package ch.tofind.commusica.file;

/**
 * @brief These constants are used to check the format of a file.
 * The arrays are the sequences of byte contained in each file of the specified format.
 * The OFFSETs are the offset of the signature in the file (for example the sequence of bytes 0x49, 0x44, 0x33 are
 * the first 3 bytes of an MP3_HEADER file).
 */
public class FilesBytes {

    public static final byte[] MP3_HEADER = {0x49, 0x44, 0x33};
    public static final int MP3_HEADER_OFFSET = 0;
    public static final String MP3_EXTENSION = "mp3";

    public static final byte[] M4A_HEADER = {0x66, 0x74, 0x79, 0x70, 0x4D, 0x34, 0x41, 0x20};
    public static final int M4A_HEADER_OFFSET = 4;
    public static final String M4A_EXTENSION = "m4a";

    public static final byte[] WAV_HEADER = {0x57, 0x41, 0x56, 0x45, 0x66, 0x6D, 0x74, 0x20};
    public static final int WAV_HEADER_OFFSET = 8;
    public static final String WAV_EXTENSION = "wav";
}
