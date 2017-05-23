package ch.tofind.commusica.ui;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.network.Server;
import ch.tofind.commusica.session.ServerSession;
import ch.tofind.commusica.session.ServerSessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.concurrent.ScheduledService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SettingsView extends AnchorPane {

    private static final String FXML_FILE = "ui/SettingsView.fxml";

    @FXML
    private ComboBox<ServerSession> serversList;

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