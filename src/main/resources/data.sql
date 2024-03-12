WITH temp_mpa AS (
    SELECT *
    FROM (
        VALUES (1, 'G'),
        (2, 'PG'),
        (3, 'PG-13'),
        (4, 'R'),
        (5, 'NC-17')
    ) AS t (mpa_rating_id, mpa_rating_name))
MERGE INTO mpa_rating mr
USING temp_mpa t
ON mr.mpa_rating_id = t.mpa_rating_id
WHEN NOT MATCHED THEN
    INSERT (mpa_rating_id, mpa_rating_name)
    VALUES (t.mpa_rating_id, t.mpa_rating_name);

WITH temp_genre AS (
    SELECT *
    FROM (
        VALUES (1, 'Комедия'),
        (2, 'Драма'),
        (3, 'Мультфильм'),
        (4, 'Триллер'),
        (5, 'Документальный'),
        (6, 'Боевик')
    ) AS t (genre_id, genre_name))
MERGE INTO genre g
USING temp_genre t
ON g.genre_id = t.genre_id
WHEN NOT MATCHED THEN
    INSERT (genre_id, genre_name)
    VALUES (t.genre_id, t.genre_name);

    INSERT INTO event_type (event_type_id, name)
                  VALUES (1, 'LIKE'),
                         (2, 'REVIEW'),
                         (3, 'FRIEND');

INSERT INTO event_operation (event_operation_id, name)
                  VALUES (1, 'ADD'),
                         (2, 'UPDATE'),
                         (3, 'REMOVE');


