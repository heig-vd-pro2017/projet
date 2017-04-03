package ch.tofind.commusica.player;

import ch.tofind.commusica.media.Track;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by Thibaut-PC on 02.04.17.
 */
public class Player  {

    public final Logger logger = Logger.getLogger(getClass().getName());
    private static final int BUFFER_SIZE = (int) Math.pow(2, 18);

    private PlayingThread playingThread;
    private PlayerManager playerManager;


    public Player(){

        Buffer buffer = new Buffer(BUFFER_SIZE);
        playingThread  = new PlayingThread(this, buffer);
        Thread t1 = new Thread(playingThread, "Playing Thread");
        t1.setPriority(Thread.MAX_PRIORITY);

        t1.start();

        //inntancié le playerManager et demarré playermanager.start()

    }

    public void open(Track track){
        // playermanager.send(Message.OPEN, track)

    }

public void play(){

        if(!isPaused()){
            Track track = getTrack();
            if(track == null){
                next();
            } else{
                //if() le track recupéré est le prochain track
                // playerMAnager.send(Message.OPEN, track)
                //else next();

            }

        }



}

public void pause(){

    playingThread.send(Actor.Message.PAUSE);
}

public void seek(long sample){

    //playerManager.send(Message.SEEK, sample)
}


public void stop(){
    //playerManager.send(Message.STOP);

}

public void next(){

    /* Track s = reupéré le prochain track à jouer
     if(s != null)

     open(s)
     else
     stop();



     */

}

public void prev(){

   /* Track s = recupéré la musique précédent

     if(s != null)
     open(s);
     else
     stop();



    */

}

public AudioOutput getAudioOutput(){
    return playingThread.getOutput();
}



    public boolean isPaused() {
        return !isPlaying() && !isStopped();
    }


    public boolean isPlaying() {
        return playingThread.isActive() && getTrack() != null;
    }

    public boolean isStopped() {
       // return !playerManager.isActive();
        return true;
    }

    public Track getTrack() {
        return playingThread.getCurrentTrack();
    }

    public void setStopAfterCurrent(boolean stopAfterCurrent) {

    }

    public long getCurrentSample() {
        return playingThread.getCurrentSample();
    }

    /*public getPlaybackOrder() {

    }*/

}


