package ch.tofind.commusica.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.logging.Logger;

public class TrackCellController {

    private static final Logger LOG = Logger.getLogger(TrackCellController.class.getName());

    private final String FXML_FILE = "trackCell.fxml";

    private String track;
    private int votes;
    private boolean fav;

    @FXML
    private AnchorPane trackPane;

    @FXML
    private Label artistLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label votesLabel;

    @FXML
    private ImageView favoriteImageView;

    public TrackCellController(String track) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_FILE));
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.track = track;

        titleLabel.setText(track);

        if(fav) {
            favoriteImageView.setImage(new Image("icons/fav_full.png"));
        } else {
            favoriteImageView.setImage(new Image("icons/fav_empty.png"));
        }
    }

    public AnchorPane getPane() {
        return trackPane;
    }

    @FXML
    private void upvote(MouseEvent event) {
        votesLabel.setText(String.valueOf(++votes));
        LOG.info(String.format("%s upvoted!", track));
    }

    @FXML
    private void downvote(MouseEvent event) {
        votesLabel.setText(String.valueOf(--votes));
        LOG.info(String.format("%s downvoted :(", track));
    }

    @FXML
    private void favorite(MouseEvent event) {
        fav = !fav;

        if(fav) {
            favoriteImageView.setImage(new Image("icons/fav_full.png"));
        } else {
            favoriteImageView.setImage(new Image("icons/fav_empty.png"));
        }

        LOG.info(String.format("%s favorited!", track));
    }
}
