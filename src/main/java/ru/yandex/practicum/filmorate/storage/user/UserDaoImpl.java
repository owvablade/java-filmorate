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
import java.util.*;

@Component
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
    public boolean delete(Long id) {
        final String sql = "DELETE FROM users WHERE user_id = ?;";
        return jdbcTemplate.update(sql, id) != 0;
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

    @Override
    public Set<Long> getRecommendedFilmsForUser(Long userId) {
        String sqlGetUser = "SELECT user_id, film_id " +
                "FROM users_likes;";
        Map<Long, Set<Long>> allUsersLikes = jdbcTemplate.query(sqlGetUser, this::mapUsersLikes);
        if (allUsersLikes == null || allUsersLikes.isEmpty())
            throw new NoSuchElementException("Лайки отсутствуют");

        Set<Long> userLikes = allUsersLikes.get(userId);
        allUsersLikes.remove(userId);

        int maxIntersectionSize = 0;
        Long userIdMax = null;
        for (var entry : allUsersLikes.entrySet()) {
            Set<Long> otherUserLikes = entry.getValue();
            int intersectionSize = getIntersectionSize(userLikes, otherUserLikes).size();
            if (intersectionSize > maxIntersectionSize) {
                maxIntersectionSize = intersectionSize;
                userIdMax = entry.getKey();
            }
        }
        Set<Long> maxIntersectionUserLikes = allUsersLikes.get(userIdMax);
        Set<Long> intersection = getIntersectionSize(userLikes, maxIntersectionUserLikes);
        maxIntersectionUserLikes.removeAll(intersection);
        return maxIntersectionUserLikes;
    }

    private Set<Long> getIntersectionSize(Set<Long> set1, Set<Long> set2) {
        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection;
    }

    private Map<Long, Set<Long>> mapUsersLikes(ResultSet rs) throws SQLException {
        Map<Long, Set<Long>> usersMapLikes = new HashMap<>();
        while (rs.next()) {
            Long userId = rs.getLong("user_id");
            Long filmId = rs.getLong("film_id");

            Set<Long> filmIds = usersMapLikes.getOrDefault(userId, new HashSet<>());
            filmIds.add(filmId);
            usersMapLikes.put(userId, filmIds);
        }
        return usersMapLikes;
    }
}
