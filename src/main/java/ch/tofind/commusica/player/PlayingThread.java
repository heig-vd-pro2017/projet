package ch.tofind.commusica.player;

import ch.tofind.commusica.media.Track;

import javax.sound.sampled.AudioFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Thibaut-PC on 02.04.17.
 */
public class PlayingThread extends Actor implements Runnable{
    public final Logger logger = Logger.getLogger(getClass().getName());
    private static final int BUFFER_SIZE = AudioOutput.BUFFER_SIZE;

    private AudioFormat format;
    private Player player;
    private final Object lock = new Object();

    private AudioOutput output = new AudioOutput();
    private Track currentTrack;
    private long currentByte;
    private boolean active = false;
    private double playbachTime;
    private long playbackBytes;
    Buffer buffer;

    public PlayingThread(Player player, Buffer buf){
        this.player = player;
        this.buffer = buf;
    }

    @Override
    protected void process(Message message) {

        switch (message){

            case PAUSE:
                setState(!active);
                  break;

            case PLAY:
                setState(true);
                break;

            case STOP:
                stop();

                break;
            case FLUSH:
                output.flush();
                break;



        }

    }


    private void setState(boolean newState) {
        if (active != newState) {
            active = newState;
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }



    private void stop() {
        output.flush();
        setState(false);
        output.close();
    }




    @Override
    public void run() {
     byte[] buf = new byte[BUFFER_SIZE];


     while(true){

         synchronized (lock) {
             try{

                 while(!active){
                     if(output.isOpen()){
                         output.stop();
                         System.gc();
                         lock.wait();

                     }
                 }

                 output.start();
                 out : while(active){
                     //je lis dans le buffer
                     int len = buffer.read(buf, 0, BUFFER_SIZE);
                     while(len == -1){
                         if(!openNext()){

                             stop();
                             break out;
                         }
                        len = buffer.read(buf, 0, BUFFER_SIZE);
                     }
                  currentByte += len;
                    playbackBytes += len;
                    output.write(buf, 0, len);
                 }

             } catch (InterruptedException e) {
                 logger.log(Level.WARNING, "Exception while playing stopping", e);
                 currentTrack = null;
                 stop();
             }


         }

     }

    }

    public long getCurrentSample() {
        return 0;
    }

     private boolean openNext(){

        return true;
     }



    public Track getCurrentTrack() {
        return currentTrack;
    }

    public AudioOutput getOutput() {
        return output;
    }

    public boolean isActive() {
        return active;
    }



}
