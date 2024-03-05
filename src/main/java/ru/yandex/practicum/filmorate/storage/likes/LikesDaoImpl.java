package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.likes.interfaces.LikesStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LikesDaoImpl implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long filmId, Long userId) {
        final String sql = "INSERT INTO users_likes (user_id, film_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public int deleteLike(Long filmId, Long userId) {
        final String sql = "DELETE FROM users_likes WHERE user_id = ? AND film_id = ?;";
        return jdbcTemplate.update(sql, userId, filmId);
    }
}
