package ru.yandex.practicum.filmorate.storage.film;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.interfaces.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("filmDaoImpl")
@RequiredArgsConstructor
public class FilmDaoImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        final String sql = "INSERT INTO films " +
                "(film_name, film_description, film_release_date, film_duration, mpa_rating_id)\n" +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);
        addGenres(film);
        return film;
    }

    @Override
    public Optional<Film> read(Long id) {
        final String sql = "SELECT f.film_id,\n" +
                "f.film_name,\n" +
                "f.film_description,\n" +
                "f.film_release_date,\n" +
                "f.film_duration,\n" +
                "f.mpa_rating_id,\n" +
                "mr.mpa_rating_name,\n" +
                "g.genre_id,\n" +
                "g.genre_name\n" +
                "FROM films AS f\n" +
                "LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id\n" +
                "LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id\n" +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                "WHERE f.film_id = ?\n" +
                "ORDER BY g.genre_id;";
        return Optional.ofNullable(jdbcTemplate.query(sql, rs -> {
            Film result = null;
            while (rs.next()) {
                if (result == null) {
                    Film film = makeFilm(rs);
                    film.setMpa(makeMpa(rs));
                    makeGenre(rs).ifPresent(film::addGenre);
                    result = film;
                } else {
                    makeGenre(rs).ifPresent(result::addGenre);
                }
            }
            return result;
        }, id));
    }

    @Override
    public Film update(Film film) {
        final String sql = "UPDATE films\n" +
                "SET film_name = ?,\n" +
                "film_description = ?,\n" +
                "film_release_date = ?,\n" +
                "film_duration = ?,\n" +
                "mpa_rating_id = ?\n" +
                "WHERE film_id = ?;";
        int rowsAffected = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (rowsAffected != 1) {
            return null;
        }
        List<Genre> genres = film.getGenres();
        if (genres != null) {
            genres = genres.stream().distinct().collect(Collectors.toList());
        }
        film.setGenres(genres);
        updateGenres(film);
        return film;
    }

    @Override
    public Film delete(Film film) {
        final String sql = "DELETE FROM films WHERE film_id = ? CASCADE;";
        if (jdbcTemplate.update(sql, film.getId()) == 0) {
            return null;
        }
        return film;
    }

    @Override
    public List<Film> getAll() {
        final String sql = "SELECT f.film_id,\n" +
                "f.film_name,\n" +
                "f.film_description,\n" +
                "f.film_release_date,\n" +
                "f.film_duration,\n" +
                "f.mpa_rating_id,\n" +
                "mr.mpa_rating_name,\n" +
                "g.genre_id,\n" +
                "g.genre_name\n" +
                "FROM films AS f\n" +
                "LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id\n" +
                "LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id\n" +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                "ORDER BY f.film_id;";
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
        });
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

    private void addGenres(Film film) {
        List<Genre> genres = film.getGenres();
        if (!genres.isEmpty()) {
            final String sql = "INSERT INTO films_genre (film_id, genre_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, film.getId());
                    ps.setInt(2, genres.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
        }
    }

    private void updateGenres(Film film) {
        final String deleteGenresSql = "DELETE FROM films_genre WHERE film_id = ?;";
        jdbcTemplate.update(deleteGenresSql, film.getId());
        addGenres(film);
    }
}
