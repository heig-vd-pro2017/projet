package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.IPlaylist;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;

import ch.tofind.commusica.utils.ObservableSortedPlaylistTrackList;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @brief Represents a graphic list view of tracks.
 *
 * Used to draw the central pane of the application.
 *
 */
public class TracksListView extends ListView<PlaylistTrack> {

    //! SavedPlaylist manager.
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

        initializeDragAndDrop();
    }

    /**
     * @brief Loads a playlist.
     *
     * Loads a playlist in the view.
     * Calls PlaylistManager#showPlaylist(SavedPlaylist).
     *
     * @param playlist SavedPlaylist to load.
     *
     * @link PlaylistManager#showPlaylist(SavedPlaylist).
     */
    public void showPlaylist(IPlaylist playlist) {
        List<PlaylistTrack> tracksList = playlist.getTracksList();

        if (!playlist.isSaved()) {
            setItems((ObservableSortedPlaylistTrackList)tracksList);
        } else {
            setItems(FXCollections.observableList(tracksList));
        }
    }

    private void initializeDragAndDrop() {
        setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }

            event.consume();
        });

        setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean status = false;

            if (dragboard.hasFiles() && dragboard.getFiles().size() == 1) {
                File file = dragboard.getFiles().get(0);

                status = true;
            }

            event.setDropCompleted(status);

            event.consume();
        });
    }
}
