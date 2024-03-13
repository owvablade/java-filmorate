with temp_mpa as (
    select *
    from (
        VALUES (1, 'G'),
        (2, 'PG'),
        (3, 'PG-13'),
        (4, 'R'),
        (5, 'NC-17')
    ) AS t (mpa_rating_id, mpa_rating_name))
merge into mpa_rating mr
using temp_mpa t
on mr.mpa_rating_id = t.mpa_rating_id
when not matched then
    insert (mpa_rating_id, mpa_rating_name)
    values (t.mpa_rating_id, t.mpa_rating_name);

with temp_genre as (
    select *
    from (
        VALUES (1, 'Комедия'),
        (2, 'Драма'),
        (3, 'Мультфильм'),
        (4, 'Триллер'),
        (5, 'Документальный'),
        (6, 'Боевик')
    ) AS t (genre_id, genre_name))
merge into genre g
using temp_genre t
on g.genre_id = t.genre_id
when not matched then
    insert (genre_id, genre_name)
    values (t.genre_id, t.genre_name);

    insert into event_type (event_type_id, name)
                  values (1, 'LIKE'),
                         (2, 'REVIEW'),
                         (3, 'FRIEND');

insert into event_operation (event_operation_id, name)
                  values (1, 'ADD'),
                         (2, 'UPDATE'),
                         (3, 'REMOVE');


