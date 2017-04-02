package ch.tofind.commusica.player;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Created by Thibaut-PC on 02.04.17.
 */


public  abstract class Actor {

    private Logger logger = Logger.getLogger(getClass().getName());


    public enum Message{

        //play message

        PLAY, PAUSE, STOP, OPEN, SEEK, FLUSH;

        private Object [] params;

        private Object[] getParams(){
            return params;
        }


        public void setParams(Object[] params){

             this.params = params;
        }
    }

    private BlockingQueue<Message> queue = new LinkedBlockingDeque<Message>();

    public synchronized void send(Message message, Object... params){

        message.setParams(params);
        queue.add(message);
    }

    protected Actor(){

        Thread messageThread = new Thread(new Runnable() {


            @Override
            public void run() {
                while (true) {

                    Message message = null;
                    try {

                        message = queue.take();
                        process(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Error processing message" + message, e);
                    }

                }
            }
        }, "Actor Thread");

        messageThread.start();



    }


    protected abstract void process(Message message);

}
