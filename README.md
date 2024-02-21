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
  1. Основной запрос:  
  ```
  SELECT *
  FROM films;
  ```
  2. Запрос для получения жанров для конректного фильма:
  ```
  SELECT genre_id
  FROM films_genre
  WHERE film_id = ?;
  ```
  или
  ```
  SELECT genre_name
  FROM films_genre
  JOIN genre ON films_genre.genre_id = genre.genre_id
  WHERE film_id = ?;
  ```
  3. Запрос для получения рейтинга:
  ```
  SELECT mpa_rating_id
  FROM films
  WHERE film_id = ?;
  ```
  или
  ```
  SELECT mpa_rating_name
  FROM mpa_rating
  WHERE mpa_rating_id =
  (SELECT mpa_rating_id
  FROM films
  WHERE film_id = ?);
  ```
- **Получение всех пользователей:**
  1. Основной запрос:
  ```
  SELECT *
  FROM users;
  ```
  2. Запрос для получения друзей:
  ```
  SELECT target_user_id AS friend_id
  FROM users_friendship
  WHERE source_user_id = ? AND is_accepted = TRUE
  UNION ALL
  SELECT source_user_id AS friend_id
  FROM users_friendship
  WHERE target_user_id = ? AND is_accepted = TRUE;
  ```
  3. Запрос для получения лайкнутых фильмов:
  ```
  SELECT film_id
  FROM users_likes
  WHERE user_id = ?;
  ```
- **Получение топ N наиболее популярных фильмов:**
```
SELECT film_id,
       COUNT(film_id)
FROM users_likes
GROUP BY film_id
ORDER BY COUNT(film_id) DESC
LIMIT N;
```
- **Получение списка общих друзей с другим пользователем:**
```
SELECT userA.friend_id
FROM
  (SELECT target_user_id AS friend_id
   FROM users_friendship
   WHERE source_user_id = ?
     AND is_accepted = TRUE
   UNION ALL 
   SELECT source_user_id AS friend_id
   FROM users_friendship
   WHERE target_user_id = ?
     AND is_accepted = TRUE) AS userA
JOIN
SELECT userB.friend_id
FROM
  (SELECT target_user_id AS friend_id
   FROM users_friendship
   WHERE source_user_id = ??
     AND is_accepted = TRUE
   UNION ALL 
   SELECT source_user_id AS friend_id
   FROM users_friendship
   WHERE target_user_id = ??
     AND is_accepted = TRUE) AS userB ON userA.friend_id = userB.friend_id;
```  
где ? - id первого пользователя, а ?? - id второго пользователя