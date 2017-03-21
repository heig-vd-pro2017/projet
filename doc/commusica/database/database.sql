BEGIN TRANSACTION;

CREATE TABLE track (
    id INTEGER NOT NULL,
    title TEXT NOT NULL,
    artist TEXT NOT NULL,
    album TEXT NOT NULL,
    length INTEGER NOT NULL,
    uri TEXT UNIQUE NOT NULL,
    date_added DATETIME NOT NULL,
    date_played DATETIME NULL,
    PRIMARY KEY(id)
);

CREATE TABLE playlist (
    id INTEGER NOT NULL,
    name text NOT NULL,
    date_added DATETIME NOT NULL,
    date_played DATETIME NULL,
    PRIMARY KEY(id)
);

CREATE TABLE playlist_track (
    playlist_id INTEGER,
    track_id INTEGER,
    FOREIGN KEY(playlist_id) REFERENCES playlist(id),
    FOREIGN KEY(track_id) REFERENCES track(id),
    PRIMARY KEY(playlist_id, track_id)
);

CREATE TABLE favorite (
    track_id INTEGER NOT NULL,
    FOREIGN KEY(track_id) REFERENCES track(id),
    PRIMARY KEY(track_id)
);

CREATE TABLE  vote (
    playlist_id INTEGER NOT NULL,
    track_id INTEGER NOT NULL,
    counted_votes INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(track_id) REFERENCES track(id),
    FOREIGN KEY(playlist_id) REFERENCES playlist(id),
    PRIMARY KEY(track_id, playlist_id)
);

COMMIT;
