package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.SavedPlaylist;
import ch.tofind.commusica.playlist.PlaylistManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * @brief This class represents
 */
public class PlaylistsListView extends AnchorPane {

    private static final String CSS_FILE = "ui/styles/PlaylistsListView.css";

    private static final String FXML_FILE = "ui/PlaylistsListView.fxml";

    //! SavedPlaylist manager.
    private static PlaylistManager manager = PlaylistManager.getInstance();

    @FXML
    Label favoritesLabel;

    @FXML
    Label playingLabel;

    @FXML
    ListView<SavedPlaylist> playlistsView;

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

        getStylesheets().add(CSS_FILE);

        playlistsView.setItems(FXCollections.observableArrayList(manager.getSavedPlaylists()));
        playlistsView.setCellFactory((ListView<SavedPlaylist> list) -> new PlaylistCell());
    }

    @FXML
    private void loadFavoritesPlaylist(MouseEvent e) {
        // Update selection.
        playingLabel.getStyleClass().remove("selected");
        playlistsView.getSelectionModel().clearSelection();
        favoritesLabel.getStyleClass().add("selected");

        UIController.getController().showPlaylist(manager.getFavoritesPlaylist());
    }

    @FXML
    private void loadPlayingPlaylist(MouseEvent e) {
        // Update selection.
        favoritesLabel.getStyleClass().remove("selected");
        playlistsView.getSelectionModel().clearSelection();
        playingLabel.getStyleClass().add("selected");

        UIController.getController().showPlaylist(manager.getPlaylist());
    }

    private class PlaylistCell extends ListCell<SavedPlaylist> {

        @Override
        public void updateItem(SavedPlaylist playlist, boolean empty) {
            super.updateItem(playlist, empty);

            if (playlist != null) {
                setText(playlist.getName());
                getStyleClass().add("subtitle");
            } else {
                setGraphic(null);
            }
        }

        @Override
        public void updateSelected(boolean value) {
            super.updateSelected(value);

            if (isSelected() && !isEmpty()) {
                favoritesLabel.getStyleClass().remove("selected");
                playingLabel.getStyleClass().remove("selected");
                UIController.getController().showPlaylist(getItem());
            }
        }
    }
}
