DROP TABLE IF EXISTS films CASCADE;

DROP TABLE IF EXISTS mpa_rating CASCADE;

DROP TABLE IF EXISTS directors CASCADE;

DROP TABLE IF EXISTS films_director CASCADE;

DROP TABLE IF EXISTS genre CASCADE;

DROP TABLE IF EXISTS films_genre CASCADE;

DROP TABLE IF EXISTS users CASCADE;

DROP TABLE IF EXISTS users_friendship CASCADE;

DROP TABLE IF EXISTS users_likes CASCADE;

DROP TABLE IF EXISTS reviews CASCADE;

DROP TABLE IF EXISTS user_reviews_likes CASCADE;

DROP TABLE IF EXISTS EVENT_TYPE;

DROP TABLE IF EXISTS EVENT_OPERATION;

DROP TABLE IF EXISTS USER_EVENT;

DROP TABLE IF EXISTS EVENT_TYPE CASCADE;

DROP TABLE IF EXISTS EVENT_OPERATION CASCADE;

DROP TABLE IF EXISTS USER_EVENT CASCADE;

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

CREATE TABLE IF NOT EXISTS reviews (
    review_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content varchar(200) NOT NULL,
    is_positive boolean NOT NULL,
    user_id bigint,
    film_id bigint
);

CREATE TABLE IF NOT EXISTS user_reviews_likes (
    user_id bigint,
    review_id bigint,
    is_useful boolean NOT NULL,
    PRIMARY KEY (user_id, review_id)
);

CREATE TABLE IF NOT EXISTS EVENT_TYPE (
    EVENT_TYPE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS EVENT_OPERATION (
    EVENT_OPERATION_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS USER_EVENT (
    EVENT_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    TIME_ADD BIGINT,
    USER_ID INTEGER REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    EVENT_TYPE_ID INTEGER REFERENCES EVENT_TYPE(EVENT_TYPE_ID) ON DELETE CASCADE ON UPDATE CASCADE,
    EVENT_OPERATION_ID INTEGER REFERENCES EVENT_OPERATION(EVENT_OPERATION_ID) ON DELETE CASCADE ON UPDATE CASCADE,
    ENTITY_ID INTEGER
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

ALTER TABLE reviews ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE reviews ADD FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE;

ALTER TABLE user_reviews_likes ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE user_reviews_likes ADD FOREIGN KEY (review_id) REFERENCES reviews (review_id) ON DELETE CASCADE;
