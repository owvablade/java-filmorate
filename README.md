# java-filmorate
## Промежуточное задание месяца SQL
### Схема БД:
![ER-диаграмма БД для бэкенда Filmorate](/assets/images/Filmorate_ER_DB.png)
#### Таблицы для фильмов:
- **films** - таблица, в которой хранится основная информация об фильмах. 
Связана с таблицей films_genre один-ко-многим.
- **films_genre** - вспомогательная таблица для реализации связи многие-ко-многим между таблицами films и genre.
Имеет составной первичный ключ.
- **genre** - таблица с жанрами фильмов. Связана с таблицей films_genre один-ко-многим.
- **mpa_rating** - таблица с рейтингами Ассоциации кинокомпаний. Связана с таблицей films один-ко-многим.
#### Таблицы для пользователей:
- **users** - таблица, в которой хранится основная информация об пользователях.
Связана с таблицей users_friendship один-ко-многим.
- **users_friendship** - таблица, в которой хранится информация обо всех дружбах между пользователями.
Имеет составной первичный ключ, обе части которого ссылаются на первичный ключ таблицы users.
#### Вспомогательные таблицы:
- **users_likes** - таблица, в которой хранится информация обо всех понравившихся фильмах пользователей. 
Представляет из себя вспомогательную таблицу для реализации связи многие-ко-многим 
между таблицами films и users.
### Примеры запросов
- **Получение всех фильмов:**
```
SELECT f.film_id,
       f.film_name,
       f.film_description,
       f.film_release_date,
       f.film_duration,
       f.mpa_rating_id,
       mr.mpa_rating_name,
       g.genre_id,
       g.genre_name
FROM films AS f
LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id
LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id
LEFT JOIN genre AS g ON fg.genre_id = g.genre_id
ORDER BY f.film_id;
```

- **Получение всех пользователей:**
```
SELECT *
FROM users;
```
- **Получение топ N наиболее популярных фильмов:**
```
SELECT f.film_id,
       f.film_name,
       f.film_description,
       f.film_release_date,
       f.film_duration,
       f.mpa_rating_id,
       mr.mpa_rating_name,
       g.genre_id,
       g.genre_name,
       COUNT(ul.film_id)
FROM users_likes ul
RIGHT JOIN films AS f ON f.film_id = ul.film_id
LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id
LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id
LEFT JOIN genre AS g ON fg.genre_id = g.genre_id
GROUP BY ul.film_id, f.film_id, g.genre_id, mr.mpa_rating_name
ORDER BY COUNT(ul.film_id) DESC
LIMIT ?;
```
- **Получение списка общих друзей с другим пользователем:**
```
SELECT u.user_id,
       u.user_email,
       u.user_login,
       u.user_name,
       u.user_birthday
FROM users_friendship uf
JOIN users u ON uf.target_user_id = u.user_id
WHERE source_user_id = ?
  AND uf.target_user_id != ?
ORDER BY user_id;
```