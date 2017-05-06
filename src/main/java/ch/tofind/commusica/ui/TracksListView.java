package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;

/**
 * @brief Represents a graphic list view of tracks.
 *
 * Used to draw the central pane of the application.
 *
 */
public class TracksListView extends ListView<PlaylistTrack> {

    //! Playlist manager.
    private static PlaylistManager playlistManager = PlaylistManager.getInstance();

    /**
     * @brief View constructor.
     *
     * Default constructor of the class.
     * Calls \ref ListView default constructor and handles loading of the FXML file.
     */
    public TracksListView() {
        super();

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/TracksListView.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setItems(FXCollections.emptyObservableList());

        setCellFactory((ListView<PlaylistTrack> view) -> new ListCell<PlaylistTrack>() {
            @Override
            public void updateItem(PlaylistTrack playlistTrack, boolean empty) {
                super.updateItem(playlistTrack, empty);

                if (playlistTrack != null) {
                    PlaylistTrackCell cell = new PlaylistTrackCell(playlistTrack);
                    setGraphic(cell.getPane());
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    /**
     * @brief Loads a playlist.
     *
     * Loads a playlist in the view.
     * Calls PlaylistManager#loadPlaylist(Playlist).
     *
     * @param playlist Playlist to load.
     *
     * @link PlaylistManager#loadPlaylist(Playlist).
     */
    public void loadPlaylist(Playlist playlist) {
        setItems(FXCollections.observableArrayList());

        playlistManager.loadPlaylist(playlist);

        setItems(playlistManager.getObservableTracksList());
    }
}
