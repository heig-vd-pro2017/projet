package ch.tofind.commusica.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class TrackCellController {

    private final String FXML_FILE = "trackCell.fxml";

    @FXML
    private AnchorPane trackPane;

    @FXML
    private Label artistLabel;

    @FXML
    private Label titleLabel;

    public TrackCellController(String track) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_FILE));
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        titleLabel.setText(track);
    }

    public AnchorPane getPane() {
        return trackPane;
    }
}
