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

    @Override
    public List<Film> getNPopular(int n) {
        final String sql = "SELECT f.film_id,\n" +
                "f.film_name,\n" +
                "f.film_description,\n" +
                "f.film_release_date,\n" +
                "f.film_duration,\n" +
                "f.mpa_rating_id,\n" +
                "mr.mpa_rating_name,\n" +
                "g.genre_id,\n" +
                "g.genre_name,\n" +
                "COUNT(ul.film_id)\n" +
                "FROM users_likes ul\n" +
                "RIGHT JOIN films AS f ON f.film_id = ul.film_id\n" +
                "LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id\n" +
                "LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id\n" +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                "GROUP BY ul.film_id, f.film_id, g.genre_id, mr.mpa_rating_name\n" +
                "ORDER BY COUNT(ul.film_id) DESC\n" +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, rs -> {
            List<Film> result = new ArrayList<>();
            Film currentFilm = null;
            while (rs.next()) {
                long currentId = rs.getLong("film_id");
                if (currentFilm == null || currentId != currentFilm.getId()) {
                    Film film = makeFilm(rs);
                    film.setMpa(makeMpa(rs));
                    makeGenre(rs).ifPresent(film::addGenre);
                    currentFilm = film;
                    result.add(currentFilm);
                } else {
                    makeGenre(rs).ifPresent(currentFilm::addGenre);
                }
            }
            return result;
        }, n);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return new Film()
                .setId(rs.getLong("film_id"))
                .setName(rs.getString("film_name"))
                .setDescription(rs.getString("film_description"))
                .setReleaseDate(rs.getDate("film_release_date").toLocalDate())
                .setDuration(rs.getInt("film_duration"));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa(rs.getInt("mpa_rating_id"), rs.getString("mpa_rating_name"));
    }

    private Optional<Genre> makeGenre(ResultSet rs) throws SQLException {
        int genreId = rs.getInt("genre_id");
        if (genreId == 0) {
            return Optional.empty();
        }
        return Optional.of(new Genre(genreId, rs.getString("genre_name")));
    }
}
