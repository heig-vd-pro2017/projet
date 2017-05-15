package ch.tofind.commusica.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serialize {

    private static final GsonBuilder builder = new GsonBuilder();

    private static final Gson gson = builder.create();

    public static String serialize(Object object) {
        return gson.toJson(object);
    }

    public static <T> T unserialize(String jsonObject, Class<T> objectClass) {
        return gson.fromJson(jsonObject, objectClass);
    }
}
