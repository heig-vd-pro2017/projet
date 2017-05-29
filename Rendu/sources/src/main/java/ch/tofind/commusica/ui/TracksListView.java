package ch.tofind.commusica.ui;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.media.IPlaylist;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

/**
 * Represents a graphic list view of tracks.
 *
 * Used to draw the central pane of the application.
 *
 */
public class TracksListView extends ListView<PlaylistTrack> {

    //! Logger for debugging.
    private static Logger LOG = new Logger(TracksListView.class.getSimpleName());

    //! SavedPlaylist manager.
    private static PlaylistManager playlistManager = PlaylistManager.getInstance();

    /**
     * View constructor.
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
     * Loads a playlist.
     *
     * Loads a playlist in the view.
     * Calls PlaylistManager#showPlaylist(SavedPlaylist).
     *
     * @param playlist SavedPlaylist to load.
     *
     * @link PlaylistManager#showPlaylist(SavedPlaylist).
     */
    public void showPlaylist(IPlaylist playlist) {
        setItems(FXCollections.observableList(playlist.getTracksList()));
    }


    /**
     * Allows the drag and drop to add tracks.
     */
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

                try {
                    AudioFile audioFile = AudioFileIO.read(file);
                    status = true;

                    Track track = new Track(audioFile);

                    if (Core.isServer()) {
                        playlistManager.getPlaylist().addTrack(track);
                    } else {
                        ArrayList<Object> args = new ArrayList<>();
                        args.add(track.getUri());

                        Core.execute(ApplicationProtocol.SEND_TRACK_REQUEST, args);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    status = false;
                }
            }

            event.setDropCompleted(status);

            event.consume();
        });
    }
}
