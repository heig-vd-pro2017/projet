package ch.tofind.commusica.core;

import ch.tofind.commusica.session.SessionManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

abstract class AbstractCore {

    public String execute(String command, ArrayList<Object> args) {

        System.out.println("Controller received the following command: " + command);

        Method method;

        // Récupère l'ID de l'utilisateur ayant effectué la commande
        String idString = String.valueOf(args.remove(0));

        Integer user = Integer.valueOf(idString);

        // Vérifie si l'utilisateur a déjà une session ou non et la crée au besoin
        SessionManager.getInstance().store(user);

        String result = "";

        try {
            method = this.getClass().getMethod( command, ArrayList.class);
            result = (String) method.invoke(this, args);
        } catch (NoSuchMethodException e) {
            result = END_OF_COMMUNICATION(args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return result;
    }

    abstract void sendUnicast(String message);

    abstract void sendMulticast(String message);

    abstract void stop();

    // Common protocol commands
    abstract String END_OF_COMMUNICATION(ArrayList<Object> args);
}
