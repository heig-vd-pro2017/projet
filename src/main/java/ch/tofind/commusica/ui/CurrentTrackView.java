package ch.tofind.commusica.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * @brief This class represents
 */
public class CurrentTrackView extends GridPane {

    //! Component CSS class.
    public static final String CSS_CLASS = "current-track-view";

    //! CSS file to apply to the view.
    private static final String CSS_FILE = "ui/styles/CurrentTrackView.css";

    //! FXML file to use for the view.
    private static final String FXML_FILE = "ui/CurrentTrackView.fxml";

    /**
     * @brief View constructor.
     */
    public CurrentTrackView() {
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
