package ch.tofind.commusica.ui;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.network.NetworkProtocol;
import ch.tofind.commusica.session.ServerSession;
import ch.tofind.commusica.session.ServerSessionManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.tofind.commusica.utils.Network;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
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
    private ComboBox<Map.Entry<String, InetAddress>> interfacesList;

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

        serversListLabel.setVisible(!Core.isServer());
        serverNameLabel.setVisible(Core.isServer());
        initializeServerNameField();
        initializeServersList();
        initializeInterfacesList();
    }

    private void initializeInterfacesList() {
        interfacesList.setItems(FXCollections.observableArrayList(new ArrayList(Network.getIPv4Interfaces().entrySet())));

        interfacesList.setCellFactory((ListView<Map.Entry<String, InetAddress>> cell) -> new InterfaceCell());
        interfacesList.setButtonCell(new InterfaceCell());

        // Set default interface as default value.
        interfacesList.setValue(Network.getIPv4Interfaces().firstEntry());

        interfacesList.valueProperty().addListener((obs, oldValue, newValue) -> {
            NetworkProtocol.interfaceToUse = newValue.getValue();
            // TODO: update multicast socket interface
        });
    }

    private void initializeServerNameField() {
        serverNameField.setVisible(Core.isServer());

        serverNameField.setText(ApplicationProtocol.serverName);
        serverNameField.setEditable(false);
        serverNameField.setDisable(true);
    }

    private void initializeServersList() {
        serversList.setItems(FXCollections.observableArrayList(ServerSessionManager.getInstance().getAvailableServers().values()));
        
        ServerSessionManager.getInstance().getAvailableServers().addListener((MapChangeListener<Integer, ServerSession>) change -> {
            serversList.setItems(FXCollections.observableArrayList(change.getMap().values()));
        });

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

    private class InterfaceCell extends ListCell<Map.Entry<String, InetAddress>> {
        public void updateItem(Map.Entry<String, InetAddress> entry, boolean empty) {
            super.updateItem(entry, empty);

            if (entry != null) {
                String interfaceName = entry.getKey();
                String ipAddress = entry.getValue().getHostAddress();

                setText(String.format("%s (%s)", interfaceName, ipAddress));
            }
            else {
                setText(null);
            }
        }
    }
}
