package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.IPlaylist;

import ch.tofind.commusica.playlist.PlaylistManager;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @brief UI controller.
 *
 * Controller meant to interact with the interface.
 */
public class UIController extends Application implements Initializable {

    private IPlaylist currentPlaylist;

    //!
    private static FXMLLoader loader = new FXMLLoader();

    //! FXML file to use for the view.
    private static final String FXML_FILE = "ui/main.fxml";

    //!
    @FXML
    private TracksListView tracksListView;

    /**
     * @brief Returns the controller for the current interface.
     *
     * @return The controller for the current interface.
     */
    public static UIController getController() {
        return loader.getController();
    }

    /**
     * @brief
     *
     * @param stage
     */
    public void start(Stage stage) {
        URL fileURL = getClass().getClassLoader().getResource(FXML_FILE);

        if (fileURL == null) {
            throw new NullPointerException("FXML file not found.");
        }

        Parent root = null;

        try {
            root = loader.load(fileURL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add("ui/styles/main.css");

        stage.setTitle("Commusica");
        stage.setScene(scene);
        stage.sizeToScene();

        stage.show();

        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
    }

    /**
     * @brief
     *
     * @param playlist
     */
    public void showPlaylist(IPlaylist playlist) {
        currentPlaylist = playlist;
        tracksListView.showPlaylist(playlist);
    }

    public void refreshPlaylist() {
        if (currentPlaylist != null) {
            tracksListView.showPlaylist(currentPlaylist);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loader.setController(this);

        showPlaylist(PlaylistManager.getInstance().getPlaylist());
    }
}
