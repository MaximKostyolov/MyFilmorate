CREATE TABLE IF NOT EXISTS users (
         user_id INTEGER AUTO_INCREMENT PRIMARY KEY,
         e_mail VARCHAR NOT NULL,
         login VARCHAR NOT NULL,
         name VARCHAR,
         birthday DATE
);

CREATE TABLE IF NOT EXISTS friendship (
         user_id INTEGER REFERENCES users (user_id),
         friend_id INTEGER REFERENCES users (user_id),
         PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS rating_mpa (
        rating_mpa_id INTEGER PRIMARY KEY,
        name VARCHAR
);

CREATE TABLE IF NOT EXISTS films (
        film_id INTEGER AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR NOT NULL,
        description VARCHAR(200) NOT NULL,
        release_date DATE,
        duration INTEGER,
        rating_mpa_id INTEGER REFERENCES rating_mpa (rating_mpa_id)
);

CREATE TABLE IF NOT EXISTS likes (
        user_id INTEGER REFERENCES users (user_id),
        film_id INTEGER REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS genre (
        genre_id INTEGER PRIMARY KEY,
        name VARCHAR
);

CREATE TABLE IF NOT EXISTS genre_films (
        film_id INTEGER REFERENCES films (film_id),
        genre_id INTEGER REFERENCES genre (genre_id),
        PRIMARY KEY (film_id, genre_id)
);