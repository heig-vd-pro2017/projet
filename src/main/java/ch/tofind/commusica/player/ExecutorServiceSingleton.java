package ch.tofind.commusica.player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Thibaut-PC on 04.04.17.
 */
public class ExecutorServiceSingleton {


    private static final ExecutorService service;

    static {
        service = Executors.newSingleThreadExecutor();
    }

    public static ExecutorService instance() {
        return service;
    }
}
