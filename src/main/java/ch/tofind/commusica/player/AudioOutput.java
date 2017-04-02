package ch.tofind.commusica.player;

import javax.sound.sampled.*;
import java.util.logging.Logger;

/**
 * Created by Thibaut-PC on 02.04.17.
 *
 */




public class AudioOutput {
    public static final int BUFFER_SIZE = (int) (Math.pow(2, 15) / 24) * 24;
    private final Logger logger = Logger.getLogger(getClass().getName());

    private SourceDataLine line;
    private FloatControl volumeControl;
    private boolean mixerChanged;
    private Mixer mixer;
    private float volume = 1f;
    private boolean linearVolume = false;


    /**
     *
     * @param fmt
     * @throws LineUnavailableException
     */


    public void init(AudioFormat fmt)  throws LineUnavailableException{

        //if it same format and the line is opened, do nothing


        if(line != null && line.isOpen()){

            if(mixerChanged || !line.getFormat().matches(fmt)){

                mixerChanged = false;
                line.drain();
                line.close();
                line = null;

            }
            else {

                return;
            }


        }

        logger.fine("audio format : " + fmt);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt, BUFFER_SIZE);
        logger.fine("Dataline info: " + info);
        if(mixer != null && mixer.isLineSupported(info)) {

            line = (SourceDataLine) mixer.getLine(info);

            logger.fine("Mixer:" + mixer.getMixerInfo().getDescription());

        }
        else {

            line = AudioSystem.getSourceDataLine(fmt);
            mixer = null;
        }
 logger.fine("Line: " + line);

    line.open(fmt, BUFFER_SIZE);
    line.start();
    if(line.isControlSupported(FloatControl.Type.VOLUME)){

        volumeControl = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
        volumeControl.setValue(volume * volumeControl.getMaximum());
        linearVolume = true;

    }
     else if(line.isControlSupported(FloatControl.Type.MASTER_GAIN)){

        volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(((float) Math.pow(10, volume / 20)));
        linearVolume = false;

    }


    }


    /**
     *
     */

    public void stop() {
        if (line != null && line.isOpen()) {
            line.stop();
        }
    }


    /**
     *
     */

      public void start(){
            if(line !=null && line.isOpen()){

                line.start();
            }

    }


    /**
     *
     */


    public void close(){
          if(line != null){

              line.close();
          }

    }


    /**
     *
     * @return
     */

    public boolean isOpen(){
        return line != null && line.isOpen();

    }

    /**
     *
     */

    public void flush (){
        if(line != null && line.isOpen()){

            line.flush();
        }

    }


    /**
     *
     * @param buf
     * @param offset
     * @param len
     */

    public void write(byte [] buf, int offset, int len){

        line.write(buf, offset, len);
    }

    public void setVolume (float volume){

        this.volume = volume;
        if(volumeControl != null){

            if(linearVolume) {

                volumeControl.setValue(volumeControl.getMaximum() * volume);
            }

            else{

                volumeControl.setValue((float) (20 * Math.log10(volume)));
            }
        }
    }


    /**
     *
      * @param actual
     * @return
     */
   public float getVolume(boolean actual){

        if(actual && volumeControl != null){
            if(linearVolume)
                return this.volumeControl.getValue()/ volumeControl.getMaximum();

            else {

                return (float) Math.pow(10, volumeControl.getValue() / 20);
            }
        }

 return volume;
   }

    /**
     *
     * @param info
     */

   public void setMixer(Mixer.Info info){
       if(info == null){

           mixer = null;
       }
       else{

           mixer = AudioSystem.getMixer(info);
       }

       mixerChanged = true;
   }


    /**
     *
     * @return
     */

   public Mixer.Info getMixer() {

       if (mixer != null) {
           return mixer.getMixerInfo();
       } else {
           return null;
       }
   }

    /**
     *
     * @return
     */

    public int available(){
         if(line != null) {
             return line.available();
         }
         else{

             return BUFFER_SIZE;
         }

    }


    /**
     *
     */

    public void drain() {

        line.drain();
    }
}
