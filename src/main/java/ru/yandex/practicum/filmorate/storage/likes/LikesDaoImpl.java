package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.likes.interfaces.LikesStorage;

@Component
@RequiredArgsConstructor
public class LikesDaoImpl implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long filmId, Long userId) {
        final String sql = "MERGE INTO users_likes ul\n" +
                "USING (VALUES (?, ?)) AS t (user_id, film_id) \n" +
                "ON ul.user_id = t.user_id AND ul.film_id = t.film_id\n" +
                "WHEN NOT MATCHED THEN\n" +
                "INSERT (user_id, film_id)\n" +
                "VALUES (t.user_id, t.film_id);";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public int deleteLike(Long filmId, Long userId) {
        final String sql = "DELETE FROM users_likes WHERE user_id = ? AND film_id = ?;";
        return jdbcTemplate.update(sql, userId, filmId);
    }
}
