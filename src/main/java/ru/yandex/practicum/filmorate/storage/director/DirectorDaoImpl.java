package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.interfaces.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DirectorDaoImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director create(Director director) {
        final String sql = "INSERT INTO directors (director_name) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"director_id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return new Director(id, director.getName());
    }

    @Override
    public Optional<Director> read(Integer id) {
        final String sql = "SELECT * FROM directors WHERE director_id = ?;";
        return jdbcTemplate.query(sql, this::makeDirector, id).stream().findFirst();
    }

    @Override
    public Director update(Director director) {
        final String sql = "UPDATE directors SET director_name = ? WHERE director_id = ?;";
        if (jdbcTemplate.update(sql, director.getName(), director.getId()) == 0) {
            return null;
        }
        return director;
    }

    @Override
    public boolean delete(Integer id) {
        final String sql = "DELETE FROM directors WHERE director_id = ?;";
        return jdbcTemplate.update(sql, id) != 0;
    }

    @Override
    public List<Director> getAll() {
        final String sql = "SELECT * FROM directors ORDER BY director_id;";
        return jdbcTemplate.query(sql, this::makeDirector);
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getInt("director_id"), rs.getString("director_name"));
    }
}
