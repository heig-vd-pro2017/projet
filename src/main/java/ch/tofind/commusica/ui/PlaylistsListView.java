package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.playlist.PlaylistManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * @brief This class represents
 */
public class PlaylistsListView extends AnchorPane {

    private static final String FXML_FILE = "ui/PlaylistsListView.fxml";

    //! Playlist manager.
    private static PlaylistManager manager = PlaylistManager.getInstance();

    @FXML
    ListView<Playlist> playlistsView;

    /**
     * @brief View constructor.
     */
    public PlaylistsListView() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_FILE));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playlistsView.setItems(FXCollections.observableArrayList(manager.getPlaylists()));
        playlistsView.setCellFactory((ListView<Playlist> list) -> new PlaylistCell());
    }

    @FXML
    private void loadPlayingPlaylist(MouseEvent e) {
        System.out.println("Not implemented yet.");
    }

    private class PlaylistCell extends ListCell<Playlist> {

        @Override
        public void updateItem(Playlist playlist, boolean empty) {
            super.updateItem(playlist, empty);

            if (playlist != null) {
                setText(playlist.getName());
            } else {
                setGraphic(null);
            }
        }

        @Override
        public void updateSelected(boolean value) {
            super.updateSelected(value);

            if (isSelected() && !isEmpty()) {
                UIController.getController().loadPlaylist(getItem());
            }
        }
    }
}
