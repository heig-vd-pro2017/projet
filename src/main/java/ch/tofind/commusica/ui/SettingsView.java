package ch.tofind.commusica.ui;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.session.ServerSession;
import ch.tofind.commusica.session.ServerSessionManager;

import java.io.IOException;

import ch.tofind.commusica.utils.Configuration;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * @brief This class represents the settings view.
 */
public class SettingsView extends AnchorPane {

    //! FXML file to use for the view.
    private static final String FXML_FILE = "ui/SettingsView.fxml";

    @FXML
    private TextField serverNameField;

    @FXML
    private Label serverNameLabel;

    @FXML
    private ComboBox<ServerSession> serversList;

    @FXML
    private Label serversListLabel;

    /**
     * @brief View constructor.
     */
    public SettingsView() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_FILE));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerSessionManager.getInstance().getAvailableServers().addListener((MapChangeListener<Integer, ServerSession>) change -> serversList.setItems(FXCollections.observableArrayList(change.getMap().values())));

        initializeServersList();
        serversListLabel.setVisible(!Core.isServer());
        initializeServersNameField();
        serverNameLabel.setVisible(Core.isServer());
    }

    private void initializeServersNameField() {
        serverNameField.setVisible(Core.isServer());

        serverNameField.setText(Configuration.getInstance().get("SERVER_NAME"));

        serverNameField.textProperty().addListener((obs, oldValue, newValue) -> {
            ApplicationProtocol.serverName = newValue;
            PlaylistManager.getInstance().getPlaylist().setName(newValue);
        });
    }

    private void initializeServersList() {
        serversList.setVisible(!Core.isServer());

        serversList.setPromptText("Select a server");

        serversList.setCellFactory((ListView<ServerSession> cell) -> new ServerSessionCell());
        serversList.setButtonCell(new ServerSessionCell());

        serversList.valueProperty().addListener((obs, oldValue, newValue) -> {
            ApplicationProtocol.serverId = newValue.getId();
            ApplicationProtocol.serverAddress = newValue.getServerIp();
            ApplicationProtocol.serverName = newValue.getServerName();

            Core.execute(ApplicationProtocol.SEND_FIRST_CONNECTION, null);
        });
    }

    /**
     * @brief Inner class that defines an item of the select list.
     */
    private class ServerSessionCell extends ListCell<ServerSession> {
        public void updateItem(ServerSession session, boolean empty) {
            super.updateItem(session, empty);

            if (session != null) {
                setText(session.getServerName());
            } else {
                setText(null);
            }
        }
    }
}
