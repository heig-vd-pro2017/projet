package ch.tofind.commusica.utils;

import ch.tofind.commusica.media.EphemeralPlaylist;
import ch.tofind.commusica.media.Player;
import ch.tofind.commusica.media.Track;

import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @brief This class is used to tell the step to un/serialize an ephemeral playlist from/to JSON.
 */
public class EphemeralPlaylistSerializer implements JsonSerializer<EphemeralPlaylist>, JsonDeserializer<EphemeralPlaylist> {

    private static final Logger LOG = new Logger(EphemeralPlaylistSerializer.class.getSimpleName());

    @Override
    public EphemeralPlaylist deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        // Playlist name.
        String name = json.getAsJsonObject().get("name").getAsString();

        PlaylistManager.getInstance().getPlaylist().setName(name);

        // To know if a playlist has been played entirely or not.
        boolean playFinished = true;

        JsonArray tracks = json.getAsJsonObject().getAsJsonArray("tracks");
        if (tracks != null) {

            Iterator<JsonElement> it = tracks.iterator();
            while (it.hasNext()) {
                JsonObject jsonPlaylistTrack = it.next().getAsJsonObject();

                String id = jsonPlaylistTrack.get("id").getAsString();
                String title = jsonPlaylistTrack.get("title").getAsString();
                String artist = jsonPlaylistTrack.get("artist").getAsString();
                String album = jsonPlaylistTrack.get("album").getAsString();
                Integer length = jsonPlaylistTrack.get("length").getAsInt();

                Integer votes = jsonPlaylistTrack.get("votes").getAsInt();

                boolean isPlaying = jsonPlaylistTrack.get("isPlaying").getAsBoolean();

                // URI is track's hash for database Unique Constraint on URI field.
                Track track = new Track(id, title, artist, album, length, id);

                PlaylistTrack playlistTrack = PlaylistManager.getInstance().getPlaylist().getPlaylistTrack(track);

                // Track already is in the ephemeral playlist.
                if (playlistTrack != null) {
                    // We update the votes count.
                    playlistTrack.getVotesProperty().setValue(votes);
                }
                // Track is new.
                else {
                    PlaylistManager.getInstance().getPlaylist().addTrack(track);
                    playlistTrack = PlaylistManager.getInstance().getPlaylist().getPlaylistTrack(track);
                    playlistTrack.getVotesProperty().setValue(votes);
                }

                if (isPlaying) {
                    playFinished = false;

                    PlaylistTrack playingTrack = Player.getCurrentPlayer().getCurrentTrackProperty().getValue();

                    if (playingTrack != playlistTrack) {
                        Player.getCurrentPlayer().getPreviousTrackProperty().setValue(playingTrack);
                    }

                    Player.getCurrentPlayer().getCurrentTrackProperty().setValue(playlistTrack);
                }
            }
        }

        if (playFinished && Player.getCurrentPlayer().getCurrentTrackProperty().getValue() != null) {
            Player.getCurrentPlayer().getPreviousTrackProperty().setValue(Player.getCurrentPlayer().getCurrentTrackProperty().getValue());
            Player.getCurrentPlayer().getCurrentTrackProperty().setValue(null);
        }

        return PlaylistManager.getInstance().getPlaylist();
    }

    @Override
    public JsonElement serialize(EphemeralPlaylist playlist, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject root = new JsonObject();

        Track playingTrack = Player.getCurrentPlayer().getCurrentTrack();

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

            playlistTrack.addProperty("isPlaying", playingTrack == item.getTrack());

            tracks.add(playlistTrack);
        });
        root.add("tracks", tracks);

        return root;
    }
}
