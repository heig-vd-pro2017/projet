package ch.tofind.commusica.utils;

import ch.tofind.commusica.media.EphemeralPlaylist;
import ch.tofind.commusica.media.Track;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EphemeralPlaylistSerializer implements JsonSerializer<EphemeralPlaylist>, JsonDeserializer<EphemeralPlaylist> {
    @Override
    public EphemeralPlaylist deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        // Playlist name.
        String name = json.getAsJsonObject().get("name").getAsString();
        Map<Track, Integer> tracksList = new HashMap<>();

        Iterator<JsonElement> it = json.getAsJsonObject().getAsJsonArray("tracks").iterator();
        while (it.hasNext()) {
            JsonObject track = it.next().getAsJsonObject();

            tracksList.put(Serialize.unserialize(track.get("track").toString(), Track.class), track.get("votes").getAsInt());
        }

        EphemeralPlaylist playlist = new EphemeralPlaylist(name);

        tracksList.forEach((track, votes) -> {
            playlist.addTrack(track);
            playlist.getPlaylistTrack(track).getVotesProperty().setValue(votes);
        });

        return playlist;
    }

    @Override
    public JsonElement serialize(EphemeralPlaylist playlist, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject root = new JsonObject();

        // Playlist name.
        root.addProperty("name", playlist.getName());

        // Playlist tracks.
        JsonArray tracks = new JsonArray();
        playlist.getTracksList().forEach(item -> {
            JsonObject track = new JsonObject();
            track.addProperty("track", Serialize.serialize(item.getTrack()));
            track.addProperty("votes", item.getVotesProperty().getValue());

            tracks.add(track);
        });

        return root;
    }
}
