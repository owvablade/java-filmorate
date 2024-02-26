package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.interfaces.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Mpa> get(Integer id) {
        final String sql = "SELECT * FROM mpa_rating WHERE mpa_rating_id = ?;";
        return jdbcTemplate.query(sql, this::makeMpa, id).stream().findFirst();
    }

    @Override
    public List<Mpa> getAll() {
        final String sql = "SELECT * FROM mpa_rating;";
        return jdbcTemplate.query(sql, this::makeMpa);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("mpa_rating_id"), rs.getString("mpa_rating_name"));
    }
}
