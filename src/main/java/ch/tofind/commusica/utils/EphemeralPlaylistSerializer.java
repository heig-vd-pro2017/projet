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

        EphemeralPlaylist playlist = new EphemeralPlaylist(name);

        JsonArray tracks = json.getAsJsonObject().getAsJsonArray("tracks");
        if (tracks != null) {
            Map<Track, Integer> tracksList = new HashMap<>();

            Iterator<JsonElement> it = tracks.iterator();
            while (it.hasNext()) {
                JsonObject playlistTrack = it.next().getAsJsonObject();

                String id = playlistTrack.get("id").getAsString();
                String title = playlistTrack.get("title").getAsString();
                String artist = playlistTrack.get("artist").getAsString();
                String album = playlistTrack.get("album").getAsString();
                Integer length = playlistTrack.get("length").getAsInt();

                Integer votes = playlistTrack.get("votes").getAsInt();

                Track track = new Track(id, title, artist, album, length, "");

                tracksList.put(track, votes);
            }

            tracksList.forEach((track, votes) -> {
                playlist.addTrack(track);
                playlist.getPlaylistTrack(track).getVotesProperty().setValue(votes);
            });
        }

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
            JsonObject playlistTrack = new JsonObject();

            playlistTrack.addProperty("id", item.getTrack().getId());
            playlistTrack.addProperty("title", item.getTrack().getTitle());
            playlistTrack.addProperty("artist", item.getTrack().getArtist());
            playlistTrack.addProperty("album", item.getTrack().getAlbum());
            playlistTrack.addProperty("length", item.getTrack().getLength());

            playlistTrack.addProperty("votes", item.getVotesProperty().getValue());

            tracks.add(playlistTrack);
        });
        root.add("tracks", tracks);

        return root;
    }
}
