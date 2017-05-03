package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.playlist.PlaylistManager;

import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * @brief This class represents
 */
public class PlaylistsListView extends ListView<Playlist> {

    //! Playlist manager.
    private static PlaylistManager manager = PlaylistManager.getInstance();

    /**
     * @brief View constructor.
     */
    public PlaylistsListView() {
        // Populate the list with playlists.
        super(FXCollections.observableArrayList(manager.getPlaylists()));

        setEditable(false);

        // Set cell factory.
        setCellFactory(cell -> new PlaylistListCell());
    }

    /**
     * @class This class represents a playlist cell in the playlist's list.
     */
    private class PlaylistListCell extends ListCell<Playlist> {

        /**
         * @brief
         *
         * @param playlist
         * @param empty
         */
        public void updateItem(Playlist playlist, boolean empty) {
            super.updateItem(playlist, empty);

            if (playlist != null) {
                setText(playlist.getName());
            }
        }

        /**
         * @brief
         *
         * @param value
         */
        public void updateSelected(boolean value) {
            super.updateSelected(value);

            if (isSelected() && !isEmpty()) {
                UIController.getController().loadPlaylist(getItem());
            }
        }
    }
}
