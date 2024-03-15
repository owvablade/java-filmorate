package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.interfaces.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewDaoImpl implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        final String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"review_id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.isPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);
        long reviewId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        review.setReviewId(reviewId);
        return review;
    }

    @Override
    public Optional<Review> read(Long id) {
        final String sql = "SELECT r.review_id,\n" +
                "r.content,\n" +
                "r.is_positive,\n" +
                "r.user_id,\n" +
                "r.film_id,\n" +
                "(SELECT COALESCE(SUM(CASE WHEN url.is_useful IS TRUE THEN 1 ELSE -1 END), 0)\n" +
                "FROM user_reviews_likes url\n" +
                "WHERE url.review_id = r.review_id) AS useful\n" +
                "FROM reviews r\n" +
                "WHERE r.review_id = ?;";
        return jdbcTemplate.query(sql, this::makeReview, id).stream().findFirst();
    }

    @Override
    public Optional<Review> update(Review newReview) {
        final String sql = "UPDATE reviews\n" +
                "SET content = ?,\n" +
                "is_positive = ?\n" +
                "WHERE review_id = ?;";
        jdbcTemplate.update(sql, newReview.getContent(), newReview.isPositive(), newReview.getReviewId());
        return read(newReview.getReviewId());
    }

    @Override
    public boolean delete(Long id) {
        final String sql = "DELETE FROM reviews WHERE review_id = ?;";
        return jdbcTemplate.update(sql, id) != 0;
    }

    @Override
    public List<Review> getAllReviews(Long filmId, Integer count) {
        String sql;
        if (filmId == null) {
            sql = "SELECT r.review_id,\n" +
                    "r.content,\n" +
                    "r.is_positive,\n" +
                    "r.user_id,\n" +
                    "r.film_id,\n" +
                    "(SELECT COALESCE(SUM(CASE WHEN url.is_useful IS TRUE THEN 1 ELSE -1 END), 0)\n" +
                    "FROM user_reviews_likes url\n" +
                    "WHERE url.review_id = r.review_id) AS useful\n" +
                    "FROM reviews r\n" +
                    "ORDER BY useful DESC\n" +
                    "LIMIT ?;";
            return jdbcTemplate.query(sql, this::makeReview, count);
        } else {
            sql = "SELECT r.review_id,\n" +
                    "r.content,\n" +
                    "r.is_positive,\n" +
                    "r.user_id,\n" +
                    "r.film_id,\n" +
                    "(SELECT COALESCE(SUM(CASE WHEN url.is_useful IS TRUE THEN 1 ELSE -1 END), 0)\n" +
                    "FROM user_reviews_likes url\n" +
                    "WHERE url.review_id = r.review_id) AS useful\n" +
                    "FROM reviews r\n" +
                    "WHERE r.film_id = ?\n" +
                    "ORDER BY useful DESC\n" +
                    "LIMIT ?;";
            return jdbcTemplate.query(sql, this::makeReview, filmId, count);
        }
    }

    @Override
    public void addLikeToReview(Long reviewId, Long userId) {
        final String sql = "INSERT INTO user_reviews_likes (review_id, user_id, is_useful) VALUES (?, ?, TRUE);";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void addDislikeToReview(Long reviewId, Long userId) {
        final String sql = "INSERT INTO user_reviews_likes (review_id, user_id, is_useful) VALUES (?, ?, FALSE);";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void removeLikeOrDislikeFromReview(Long reviewId, Long userId) {
        final String sql = "DELETE FROM user_reviews_likes WHERE review_id = ? AND user_id = ?;";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review(rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getLong("user_id"),
                rs.getLong("film_id"));
        review.setReviewId(rs.getLong("review_id"));
        review.setUseful(rs.getInt("useful"));
        return review;
    }
}
