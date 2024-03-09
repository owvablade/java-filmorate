DROP TABLE IF EXISTS users_friendship;

DROP TABLE IF EXISTS users_likes;

DROP TABLE IF EXISTS users;

DROP TABLE IF EXISTS films_genre;

DROP TABLE IF EXISTS films_director;

DROP TABLE IF EXISTS genre;

DROP TABLE IF EXISTS films;

DROP TABLE IF EXISTS mpa_rating;

DROP TABLE IF EXISTS directors;

CREATE TABLE IF NOT EXISTS films (
    film_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name varchar(50) NOT NULL,
    film_description varchar(200),
    film_release_date date,
    film_duration integer,
    mpa_rating_id integer
);

CREATE TABLE IF NOT EXISTS mpa_rating (
    mpa_rating_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_rating_name varchar(10)
);

CREATE TABLE IF NOT EXISTS directors (
    director_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS films_director (
    film_id bigint,
    director_id integer,
    PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name varchar(50)
);

CREATE TABLE IF NOT EXISTS films_genre (
    film_id bigint,
    genre_id integer,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_email varchar(50) NOT NULL,
    user_login varchar(50) NOT NULL,
    user_name varchar(50),
    user_birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS users_likes (
    user_id bigint,
    film_id bigint,
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS users_friendship (
    source_user_id bigint,
    target_user_id bigint,
    PRIMARY KEY (source_user_id, target_user_id)
);

ALTER TABLE films ADD FOREIGN KEY (mpa_rating_id) REFERENCES mpa_rating (mpa_rating_id) ON DELETE CASCADE;

ALTER TABLE films_director ADD FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE;

ALTER TABLE films_director ADD FOREIGN KEY (director_id) REFERENCES directors (director_id) ON DELETE CASCADE;

ALTER TABLE films_genre ADD FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE;

ALTER TABLE films_genre ADD FOREIGN KEY (genre_id) REFERENCES genre (genre_id) ON DELETE CASCADE;

ALTER TABLE users_likes ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE users_likes ADD FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE;

ALTER TABLE users_friendship ADD FOREIGN KEY (source_user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE users_friendship ADD FOREIGN KEY (target_user_id) REFERENCES users (user_id) ON DELETE CASCADE;