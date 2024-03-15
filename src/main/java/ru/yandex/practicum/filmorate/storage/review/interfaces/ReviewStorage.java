package ru.yandex.practicum.filmorate.storage.review.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Optional<Review> read(Long id);

    Optional<Review> update(Review newReview);

    boolean delete(Long id);

    List<Review> getAllReviews(Long filmId, Integer count);

    void addLikeToReview(Long reviewId, Long userId);

    void addDislikeToReview(Long reviewId, Long userId);

    void removeLikeOrDislikeFromReview(Long reviewId, Long userId);
}
