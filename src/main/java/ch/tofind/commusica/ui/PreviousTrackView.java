package ch.tofind.commusica.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class PreviousTrackView extends GridPane {

    //! CSS class value.
    public static final String CSS_CLASS = "previous-track-view";

    private static final String CSS_FILE = "ui/styles/PreviousTrackView.css";

    private static final String FXML_FILE = "ui/PreviousTrackView.fxml";

    public PreviousTrackView() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_FILE));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getStyleClass().add(CSS_CLASS);
        getStylesheets().add(CSS_FILE);
    }

}
