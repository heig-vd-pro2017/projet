package ch.tofind.commusica.ui;

import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.media.IPlaylist;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.Network;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @brief UI controller.
 *
 * Controller meant to interact with the interface.
 */
public class UIController extends Application implements Initializable {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(UIController.class.getSimpleName());

    //! Current playlist.
    private IPlaylist currentPlaylist;

    //! Loader to load FXML files.
    private static FXMLLoader loader = new FXMLLoader();

    //! FXML file to use for the view.
    private static final String FXML_FILE = "ui/main.fxml";

    @FXML
    private PlaylistsListView playlistsListView;

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
     * @brief Display the playlist.
     *
     * @param playlist The playlist to display.
     */
    public void showPlaylist(IPlaylist playlist) {
        currentPlaylist = playlist;
        tracksListView.showPlaylist(playlist);
    }

    /**
     * @brief Refresh the current playlist.
     */
    public void refreshPlaylist() {
        if (currentPlaylist != null) {
            tracksListView.showPlaylist(currentPlaylist);
        }
    }

    /**
     * @brief Refresh the playlists' list.
     */
    public void refreshPlaylistsList() {
        playlistsListView.refresh();
    }

    /**
     * @brief Get the current playlist.
     *
     * @return The current playlist.
     */
    public IPlaylist getCurrentPlaylist() {
        return currentPlaylist;
    }

    /**
     * @brief Displays a popup on the main window with the given message.
     *
     * @param message The message of the popup.
     */
    public void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    /**
     * @brief Display the dialog to choose if we launch the application as server or client.
     */
    private void displayServerClientDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setContentText("Welcome to Commusica!\n\nWould you like to be the server?");
        dialog.setHeaderText(null);
        dialog.setTitle("Welcome!");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        dialog.getDialogPane().getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent()) {
            Network.configureNetwork();

            if (result.get() == yesButton) {
                LOG.info("Launching as server.");
                Core.setupAsServer();
            }
            else {
                LOG.info("Launching as client.");
                Core.setupAsClient();
            }
        } else {

            LOG.error("Quitting application because user didn't choose an option.");

            Platform.exit();
        }
    }

    @Override
    public void start(Stage stage) {
        URL fileURL = getClass().getClassLoader().getResource(FXML_FILE);

        if (fileURL == null) {
            throw new NullPointerException("FXML file not found.");
        }

        Parent root;

        try {
            root = FXMLLoader.load(fileURL);
        } catch (Exception e) {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loader.setController(this);

        // Display client/server choice.
        displayServerClientDialog();

        // Select the Playing playlist.
        showPlaylist(PlaylistManager.getInstance().getPlaylist());
    }

    @Override
    public void stop() {
        LOG.info("Stopping application...");
        Core.stop();
    }
}
