DROP TABLE IF EXISTS mpa_rating CASCADE;

DROP TABLE IF EXISTS directors CASCADE;

DROP TABLE IF EXISTS genre CASCADE;

DROP TABLE IF EXISTS films CASCADE;

DROP TABLE IF EXISTS films_director CASCADE;

DROP TABLE IF EXISTS films_genre CASCADE;

DROP TABLE IF EXISTS users CASCADE;

DROP TABLE IF EXISTS users_likes CASCADE;

DROP TABLE IF EXISTS users_friendship CASCADE;

DROP TABLE IF EXISTS reviews CASCADE;

DROP TABLE IF EXISTS user_reviews_likes CASCADE;

DROP TABLE IF EXISTS event_type CASCADE;

DROP TABLE IF EXISTS event_operation CASCADE;

DROP TABLE IF EXISTS user_event CASCADE;

CREATE TABLE IF NOT EXISTS mpa_rating (
    mpa_rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_rating_name VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS directors (
    director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name VARCHAR(50) NOT NULL,
    film_description VARCHAR(200),
    film_release_date DATE,
    film_duration INTEGER,
    mpa_rating_id INTEGER REFERENCES mpa_rating (mpa_rating_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS films_director (
    film_id BIGINT REFERENCES films (film_id) ON DELETE CASCADE,
    director_id INTEGER REFERENCES directors (director_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS films_genre (
    film_id BIGINT REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genre (genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_email VARCHAR(50) NOT NULL,
    user_login VARCHAR(50) NOT NULL,
    user_name VARCHAR(50),
    user_birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS users_likes (
    user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    film_id BIGINT REFERENCES films (film_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS users_friendship (
    source_user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    target_user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    PRIMARY KEY (source_user_id, target_user_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR(200) NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    film_id BIGINT REFERENCES films (film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_reviews_likes (
    user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    review_id BIGINT REFERENCES reviews (review_id) ON DELETE CASCADE,
    is_useful BOOLEAN NOT NULL,
    PRIMARY KEY (user_id, review_id)
);

CREATE TABLE IF NOT EXISTS event_type (
    event_type_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS event_operation (
    event_operation_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS user_event (
    event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    time_add BIGINT,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    event_type_id INTEGER REFERENCES event_type (event_type_id) ON DELETE CASCADE ON UPDATE CASCADE,
    event_operation_id INTEGER REFERENCES event_operation (event_operation_id) ON DELETE CASCADE ON UPDATE CASCADE,
    entity_id INTEGER
);
