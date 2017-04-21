package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class UIController extends Application implements Initializable {

    private static FXMLLoader loader = new FXMLLoader();

    private static Logger LOG = Logger.getLogger(UIController.class.getName());

    private static final String FXFILE = "ui/main.fxml";

    //! JavaFX components.
    @FXML
    private ListView<PlaylistTrack> tracksListView;

    public static UIController getController() {
        return loader.getController();
    }

    public void start(Stage stage) throws Exception {
        URL fileURL = getClass().getClassLoader().getResource(FXFILE);

        if (fileURL == null) {
            throw new NullPointerException("FXML file not found.");
        }

        Parent root = loader.load(fileURL);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("ui/styles/main.css");

        stage.setTitle("Commusica");
        stage.setScene(scene);
        stage.sizeToScene();

        stage.show();

        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
    }

    void loadPlaylist(Playlist playlist) {
        tracksListView.setItems(FXCollections.observableArrayList());

        if(playlist != null) {
            PlaylistManager.getInstance().loadPlaylist(playlist);
            tracksListView.setItems(FXCollections.observableArrayList(PlaylistManager.getInstance().getPlaylistTracks()));
        }

        tracksListView.setCellFactory((ListView<PlaylistTrack> view) -> new ListCell<PlaylistTrack>() {
            @Override
            public void updateItem(PlaylistTrack item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    PlaylistTrackCell cell = new PlaylistTrackCell(item);
                    setGraphic(cell.getPane());
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loader.setController(this);

        loadPlaylist(null);
    }
}
