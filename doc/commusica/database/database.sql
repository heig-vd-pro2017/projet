BEGIN TRANSACTION;

DROP TABLE IF EXISTS track;
CREATE TABLE track (
    id INTEGER NOT NULL,
    title VARCHAR(50) NOT NULL,
    artist VARCHAR(50) NOT NULL,
    album VARCHAR(50) NOT NULL,
    length INTEGER NOT NULL,
    uri VARCHAR(500) UNIQUE NOT NULL,
    date_added DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_played DATETIME NULL,
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS playlist;
CREATE TABLE playlist (
    id INTEGER NOT NULL,
    name VARCHAR(50) NOT NULL,
    date_added DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_played DATETIME NULL,
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS playlist_track;
CREATE TABLE playlist_track (
    playlist_id INTEGER,
    track_id INTEGER,
    FOREIGN KEY(playlist_id) REFERENCES playlist(id),
    FOREIGN KEY(track_id) REFERENCES track(id),
    PRIMARY KEY(playlist_id, track_id)
);

DROP TABLE IF EXISTS favorite;
CREATE TABLE favorite (
    track_id INTEGER NOT NULL,
    FOREIGN KEY(track_id) REFERENCES track(id),
    PRIMARY KEY(track_id)
);

DROP TABLE IF EXISTS vote;
CREATE TABLE  vote (
    playlist_id INTEGER NOT NULL,
    track_id INTEGER NOT NULL,
    counted_votes INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(track_id) REFERENCES track(id),
    FOREIGN KEY(playlist_id) REFERENCES playlist(id),
    PRIMARY KEY(track_id, playlist_id)
);

COMMIT;
