package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.playlist.PlaylistManager;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class PlaylistsListView extends ListView<Playlist> {

    private static PlaylistManager manager = PlaylistManager.getInstance();

    public PlaylistsListView() {
        // Populate the list with playlists.
        super(FXCollections.observableArrayList(manager.getPlaylists()));

        setEditable(false);

        // Set cell factory.
        setCellFactory(cell -> new PlaylistListCell());
    }

    private class PlaylistListCell extends ListCell<Playlist> {

        public void updateItem(Playlist playlist, boolean empty) {
            super.updateItem(playlist, empty);

            if (playlist != null) {
                setText(playlist.getName());
            }
        }

        public void updateSelected(boolean value) {
            super.updateSelected(value);

            if (isSelected() && !isEmpty()) {
                UIController.getController().loadPlaylist(getItem());
            }
        }
    }
}
