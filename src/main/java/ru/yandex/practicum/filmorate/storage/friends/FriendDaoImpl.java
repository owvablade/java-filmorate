package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.interfaces.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendDaoImpl implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Long userId, Long friendId) {
        final String sql = "INSERT INTO users_friendship (source_user_id, target_user_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        final String sql = "DELETE FROM users_friendship WHERE source_user_id = ? AND target_user_id = ?;";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        final String sql = "SELECT u.user_id,\n" +
                "u.user_email,\n" +
                "u.user_login,\n" +
                "u.user_name,\n" +
                "u.user_birthday\n" +
                "FROM users_friendship uf\n" +
                "JOIN users u ON uf.target_user_id = u.user_id\n" +
                "WHERE source_user_id = ?\n" +
                "ORDER BY u.user_id;";
        return jdbcTemplate.query(sql, this::makeUser, id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        final String sql = "SELECT u.user_id, u.user_email, u.user_login, u.user_name, u.user_birthday \n" +
                "FROM users_friendship uf\n" +
                "JOIN users u ON uf.target_user_id = u.user_id\n" +
                "WHERE source_user_id = ? AND uf.target_user_id != ?\n" +
                "ORDER BY user_id;";
        return jdbcTemplate.query(sql, this::makeUser, id, otherId);
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
