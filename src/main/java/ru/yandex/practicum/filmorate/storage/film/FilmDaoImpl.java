package ru.yandex.practicum.filmorate.storage.film;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.interfaces.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
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
        addDirectors(film);
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
                "LISTAGG(DISTINCT g.genre_id, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_id,\n" +
                "LISTAGG(DISTINCT g.genre_name, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_name,\n" +
                "LISTAGG(DISTINCT d.director_id, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_id,\n" +
                "LISTAGG(DISTINCT d.director_name, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_name\n" +
                "FROM films AS f\n" +
                "LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id\n" +
                "LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id\n" +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                "LEFT JOIN films_director AS fd ON fd.film_id = f.film_id\n" +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
                "WHERE f.film_id = ?\n" +
                "GROUP BY f.film_id\n" +
                "ORDER BY f.film_id;";
        return jdbcTemplate.query(sql, this::makeFilm, id).stream().findFirst();
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
        if (genres != null && !genres.isEmpty()) {
            genres = genres.stream()
                    .distinct()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toList());
            film.setGenres(genres);
        }
        List<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            directors = directors.stream()
                    .distinct()
                    .sorted(Comparator.comparing(Director::getId))
                    .collect(Collectors.toList());
            film.setDirectors(directors);
        }
        updateGenres(film);
        updateDirectors(film);
        return film;
    }

    @Override
    public boolean delete(Long id) {
        final String sql = "DELETE FROM films WHERE film_id = ?;";
        return jdbcTemplate.update(sql, id) != 0;
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
                "LISTAGG(DISTINCT g.genre_id, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_id,\n" +
                "LISTAGG(DISTINCT g.genre_name, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_name,\n" +
                "LISTAGG(DISTINCT d.director_id, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_id,\n" +
                "LISTAGG(DISTINCT d.director_name, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_name\n" +
                "FROM films AS f\n" +
                "LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id\n" +
                "LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id\n" +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                "LEFT JOIN films_director AS fd ON fd.film_id = f.film_id\n" +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
                "GROUP BY f.film_id\n" +
                "ORDER BY f.film_id;";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    public List<Film> getMostNPopular(int n) {
        final String sql = "SELECT f.film_id,\n" +
                "f.film_name,\n" +
                "f.film_description,\n" +
                "f.film_release_date,\n" +
                "f.film_duration,\n" +
                "f.mpa_rating_id,\n" +
                "mr.mpa_rating_name,\n" +
                "LISTAGG(DISTINCT g.genre_id, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_id,\n" +
                "LISTAGG(DISTINCT g.genre_name, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_name,\n" +
                "LISTAGG(DISTINCT d.director_id, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_id,\n" +
                "LISTAGG(DISTINCT d.director_name, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_name,\n" +
                "COUNT(DISTINCT ul.user_id) AS cnt\n" +
                "FROM films f\n" +
                "LEFT JOIN users_likes AS ul ON f.film_id = ul.film_id\n" +
                "LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id\n" +
                "LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id\n" +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                "LEFT JOIN films_director AS fd ON fd.film_id = f.film_id\n" +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
                "GROUP BY f.film_id\n" +
                "ORDER BY cnt DESC\n" +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, this::makeFilm, n);
    }

    @Override
    public List<Film> getMostNPopularBy(int n, String genreId, String year) {
        final String genreIdForSqlQuery = "%" + genreId + "%";
        final String byYearSql = "WHERE EXTRACT(YEAR FROM f.film_release_date) = ?\n";
        final String byGenreIdSql = "HAVING LISTAGG(DISTINCT g.genre_id, ',') LIKE ?\n";
        final String sql = "SELECT f.film_id,\n" +
                "f.film_name,\n" +
                "f.film_description,\n" +
                "f.film_release_date,\n" +
                "f.film_duration,\n" +
                "f.mpa_rating_id,\n" +
                "mr.mpa_rating_name,\n" +
                "LISTAGG(DISTINCT g.genre_id, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_id,\n" +
                "LISTAGG(DISTINCT g.genre_name, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_name,\n" +
                "LISTAGG(DISTINCT d.director_id, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_id,\n" +
                "LISTAGG(DISTINCT d.director_name, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_name\n" +
                "FROM films f\n" +
                "LEFT JOIN users_likes AS ul ON f.film_id = ul.film_id\n" +
                "LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id\n" +
                "LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id\n" +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                "LEFT JOIN films_director AS fd ON fd.film_id = f.film_id\n" +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
                "%s" +
                "GROUP BY f.film_id\n" +
                "%s" +
                "ORDER BY COUNT(DISTINCT ul.user_id) DESC\n" +
                "LIMIT ?;";
        if (genreId != null && year != null) {
            return jdbcTemplate.query(String.format(sql, byYearSql, byGenreIdSql),
                    this::makeFilm,
                    year, genreIdForSqlQuery, n);
        } else if (genreId != null) {
            return jdbcTemplate.query(String.format(sql, "", byGenreIdSql),
                    this::makeFilm,
                    genreIdForSqlQuery, n);
        } else {
            return jdbcTemplate.query(String.format(sql, byYearSql, ""),
                    this::makeFilm,
                    year, n);
        }
    }

    public List<Film> getAllByDirector(Integer directorId, String sort) {
        String sql;
        if (sort.equals("likes")) {
            sql = "SELECT f.film_id,\n" +
                    "f.film_name,\n" +
                    "f.film_description,\n" +
                    "f.film_release_date,\n" +
                    "f.film_duration,\n" +
                    "f.mpa_rating_id,\n" +
                    "mr.mpa_rating_name,\n" +
                    "LISTAGG(DISTINCT g.genre_id, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_id,\n" +
                    "LISTAGG(DISTINCT g.genre_name, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_name,\n" +
                    "LISTAGG(DISTINCT d.director_id, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_id,\n" +
                    "LISTAGG(DISTINCT d.director_name, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_name,\n" +
                    "COUNT(DISTINCT ul.user_id) AS cnt\n" +
                    "FROM films f\n" +
                    "LEFT JOIN users_likes AS ul ON f.film_id = ul.film_id\n" +
                    "LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id\n" +
                    "LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id\n" +
                    "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                    "LEFT JOIN films_director AS fd ON fd.film_id = f.film_id\n" +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
                    "WHERE fd.director_id = ?\n" +
                    "GROUP BY f.film_id\n" +
                    "ORDER BY cnt DESC;";
        } else {
            sql = "SELECT f.film_id,\n" +
                    "f.film_name,\n" +
                    "f.film_description,\n" +
                    "f.film_release_date,\n" +
                    "f.film_duration,\n" +
                    "f.mpa_rating_id,\n" +
                    "mr.mpa_rating_name,\n" +
                    "LISTAGG(DISTINCT g.genre_id, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_id,\n" +
                    "LISTAGG(DISTINCT g.genre_name, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_name,\n" +
                    "LISTAGG(DISTINCT d.director_id, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_id,\n" +
                    "LISTAGG(DISTINCT d.director_name, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_name\n" +
                    "FROM films f\n" +
                    "LEFT JOIN users_likes AS ul ON f.film_id = ul.film_id\n" +
                    "LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id\n" +
                    "LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id\n" +
                    "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                    "LEFT JOIN films_director AS fd ON fd.film_id = f.film_id\n" +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
                    "WHERE fd.director_id = ?\n" +
                    "GROUP BY f.film_id\n" +
                    "ORDER BY f.film_release_date;";
        }
        return jdbcTemplate.query(sql, this::makeFilm, directorId);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        final String sql = "SELECT f.film_id,\n" +
                "f.film_name,\n" +
                "f.film_description,\n" +
                "f.film_release_date,\n" +
                "f.film_duration,\n" +
                "f.mpa_rating_id,\n" +
                "mr.mpa_rating_name,\n" +
                "LISTAGG(DISTINCT g.genre_id, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_id,\n" +
                "LISTAGG(DISTINCT g.genre_name, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_name,\n" +
                "LISTAGG(DISTINCT d.director_id, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_id,\n" +
                "LISTAGG(DISTINCT d.director_name, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_name,\n" +
                "COUNT(DISTINCT ul.user_id) AS cnt\n" +
                "FROM users_likes AS ul\n" +
                "LEFT JOIN films AS f ON f.film_id = ul.film_id\n" +
                "LEFT JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id\n" +
                "LEFT JOIN films_genre AS fg ON f.film_id = fg.film_id\n" +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id\n" +
                "LEFT JOIN films_director AS fd ON fd.film_id = f.film_id\n" +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id\n" +
                "WHERE ul.film_id IN (\n" +
                "SELECT film_id FROM users_likes ul WHERE user_id = ?\n" +
                "INTERSECT\n" +
                "SELECT film_id FROM users_likes ul WHERE user_id = ?\n" +
                ")\n" +
                "GROUP BY f.film_id\n" +
                "ORDER BY cnt DESC;";
        return jdbcTemplate.query(sql, this::makeFilm, userId, friendId);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film()
                .setId(rs.getLong("film_id"))
                .setName(rs.getString("film_name"))
                .setDescription(rs.getString("film_description"))
                .setReleaseDate(rs.getDate("film_release_date").toLocalDate())
                .setDuration(rs.getInt("film_duration"))
                .setMpa(makeMpa(rs))
                .setGenres(makeGenres(rs))
                .setDirectors(makeDirectors(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        int mpaId = rs.getInt("mpa_rating_id");
        if (mpaId == 0) {
            return null;
        }
        return new Mpa(mpaId, rs.getString("mpa_rating_name"));
    }

    private List<Genre> makeGenres(ResultSet rs) throws SQLException {
        List<Genre> result = new ArrayList<>();
        String ids = rs.getString("genre_id");
        if (ids == null) {
            return result;
        }
        String[] genreIds = ids.split(",");
        String[] genreNames = rs.getString("genre_name").split(",");
        for (int i = 0; i < genreIds.length; i++) {
            result.add(new Genre(Integer.parseInt(genreIds[i]), genreNames[i]));
        }
        return result;
    }

    private List<Director> makeDirectors(ResultSet rs) throws SQLException {
        List<Director> result = new ArrayList<>();
        String ids = rs.getString("director_id");
        if (ids == null) {
            return result;
        }
        String[] directorIds = ids.split(",");
        String[] directorNames = rs.getString("director_name").split(",");
        for (int i = 0; i < directorIds.length; i++) {
            result.add(new Director(Integer.parseInt(directorIds[i]), directorNames[i]));
        }
        return result;
    }

    private void updateGenres(Film film) {
        final String deleteGenresSql = "DELETE FROM films_genre WHERE film_id = ?;";
        jdbcTemplate.update(deleteGenresSql, film.getId());
        addGenres(film);
    }

    private void updateDirectors(Film film) {
        final String deleteDirectorsSql = "DELETE FROM films_director WHERE film_id = ?;";
        jdbcTemplate.update(deleteDirectorsSql, film.getId());
        addDirectors(film);
    }

    private void addGenres(Film film) {
        List<Genre> genres = film.getGenres();
        if (!genres.isEmpty()) {
            final String sql = "INSERT INTO films_genre (film_id, genre_id) VALUES (?, ?);";
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

    private void addDirectors(Film film) {
        List<Director> directors = film.getDirectors();
        if (!directors.isEmpty()) {
            final String sql = "INSERT INTO films_director (film_id, director_id) VALUES (?, ?);";
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, film.getId());
                    ps.setInt(2, directors.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return directors.size();
                }
            });
        }
    }

    @Override
    public List<Film> getFilmBySearch(String query, String by) {
        StringBuilder sql = new StringBuilder("SELECT "
                + "f.film_id,"
                + "f.film_name,"
                + "f.film_description,"
                + "f.film_release_date,"
                + "f.film_duration,"
                + "f.mpa_rating_id,"
                + "m.mpa_rating_name, "
                + "LISTAGG(DISTINCT g.genre_id, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_id, "
                + "LISTAGG(DISTINCT g.genre_name, ',') WITHIN GROUP (ORDER BY g.genre_id) AS genre_name, "
                + "LISTAGG(DISTINCT d.director_id, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_id, "
                + "LISTAGG(DISTINCT d.director_name, ',') WITHIN GROUP (ORDER BY d.director_id) AS director_name "
                + "FROM films f "
                + "LEFT JOIN users_likes ul ON f.film_id = ul.film_id "
                + "LEFT JOIN mpa_rating m ON m.mpa_rating_id = f.mpa_rating_id "
                + "LEFT JOIN films_director fd ON f.film_id = fd.film_id "
                + "LEFT JOIN directors d ON fd.director_id = d.director_id "
                + "LEFT JOIN films_genre fg ON f.film_id = fg.film_id "
                + "LEFT JOIN genre g ON fg.genre_id = g.genre_id ");
        if (by.equals("title")) {
            sql.append("WHERE LOWER(f.film_name) LIKE LOWER('%").append(query).append("%') ");
        }
        if (by.equals("director")) {
            sql.append("WHERE LOWER(d.director_name) LIKE LOWER('%").append(query).append("%') ");
        }
        if (by.equals("title,director") || by.equals("director,title")) {
            sql.append("WHERE LOWER(f.film_name) LIKE LOWER('%").append(query).append("%') ");
            sql.append("OR LOWER(d.director_name) LIKE LOWER('%").append(query).append("%') ");
        }
        sql.append("GROUP BY f.film_id, ul.film_id " + "ORDER BY COUNT(ul.film_id) DESC;");
        return jdbcTemplate.query(sql.toString(), this::makeFilm);
    }
}
