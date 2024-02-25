package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.interfaces.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("userDaoImpl")
@RequiredArgsConstructor
public class UserDaoImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        final String sql = "INSERT INTO users (user_email, user_login, user_name, user_birthday) VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(userId);
        return user;
    }

    @Override
    public Optional<User> read(Long id) {
        final String sql = "SELECT * FROM users WHERE user_id = ?;";
        return jdbcTemplate.query(sql, this::makeUser, id).stream().findFirst();
    }

    @Override
    public User update(User user) {
        final String sql = "UPDATE users\n" +
                "SET user_email = ?,\n" +
                "user_login = ?,\n" +
                "user_name = ?,\n" +
                "user_birthday = ?\n" +
                "WHERE user_id = ?;";
        int rowsAffected = jdbcTemplate.update(sql,
                user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getId());
        if (rowsAffected == 0) {
            return null;
        }
        return user;
    }

    @Override
    public User delete(User user) {
        final String sql = "DELETE FROM users WHERE user_id = ? CASCADE;";
        if (jdbcTemplate.update(sql, user.getId()) == 0) {
            return null;
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        final String sql = "SELECT * FROM users;";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User()
                .setId(rs.getLong("user_id"))
                .setEmail(rs.getString("user_email"))
                .setLogin(rs.getString("user_login"))
                .setName(rs.getString("user_name"))
                .setBirthday(rs.getDate("user_birthday").toLocalDate());
    }
}
