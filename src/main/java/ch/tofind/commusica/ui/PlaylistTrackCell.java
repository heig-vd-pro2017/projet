package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class PlaylistTrackCell {

    private static final Logger LOG = new Logger(PlaylistTrackCell.class.getSimpleName());

    private static final String FXML_FILE = "ui/PlaylistTrackCell.fxml";

    private PlaylistTrack playlistTrack;
    private Track track;

    private boolean fav;

    @FXML
    private AnchorPane trackPane;

    @FXML
    private Label albumLabel;

    @FXML
    private Label artistLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label votesLabel;

    @FXML
    private ImageView favoriteImageView;

    public PlaylistTrackCell(PlaylistTrack playlistTrack) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_FILE));
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.playlistTrack = playlistTrack;
        this.track = playlistTrack.getTrack();

        titleLabel.setText(track.getTitle());
        artistLabel.setText(track.getArtist());
        albumLabel.setText(track.getAlbum());

        votesLabel.setText(String.valueOf(playlistTrack.getVotes()));

        if(fav) {
            favoriteImageView.setImage(new Image("ui/icons/fav_full.png"));
        } else {
            favoriteImageView.setImage(new Image("ui/icons/fav_empty.png"));
        }
    }

    public AnchorPane getPane() {
        return trackPane;
    }

    @FXML
    private void upvote(MouseEvent event) {
        playlistTrack.upvote();

        votesLabel.setText(String.valueOf(playlistTrack.getVotes()));
    }

    @FXML
    private void downvote(MouseEvent event) {
        playlistTrack.downvote();

        votesLabel.setText(String.valueOf(playlistTrack.getVotes()));
    }

    @FXML
    private void favorite(MouseEvent event) {
        fav = !fav;

        if (fav) {
            favoriteImageView.setImage(new Image("ui/icons/fav_full.png"));
        } else {
            favoriteImageView.setImage(new Image("ui/icons/fav_empty.png"));
        }

        LOG.info(String.format("%s favorited!", track));
    }
}
