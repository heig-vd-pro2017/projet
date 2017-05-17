package ch.tofind.commusica.file;

/**
 * @brief These constants are used to check the format of a file.
 * The arrays are the sequences of byte contained in each file of the specified format.
 * The OFFSETs are the offset of the signature in the file (for example the sequence of bytes 0x49, 0x44, 0x33 are
 * the first 3 bytes of an MP3 file).
 */
public class AudioFilesOffset {

    public static final byte[] MP3 = {0x49, 0x44, 0x33};
    public static final int OFFSET_MP3_SIGNATURE = 0;

    public static final byte[] M4A = {0x66, 0x74, 0x79, 0x70, 0x4D, 0x34, 0x41, 0x20};
    public static final int OFFSET_M4A_SIGNATURE = 4;

    public static final byte[] WAV = {0x57, 0x41, 0x56, 0x45, 0x66, 0x6D, 0x74, 0x20};
    public static final int OFFSET_WAV_SIGNATURE = 8;
}
