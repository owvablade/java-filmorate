package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.review.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.film.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.interfaces.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.interfaces.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewStorage reviewStorage;

    private final EventService eventService;


    public Review createReview(Review review) {
        userStorage.read(review.getUserId()).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id=%d not found", review.getUserId())));
        filmStorage.read(review.getFilmId()).orElseThrow(() ->
                new FilmNotFoundException(String.format("Film with id=%d not found", review.getFilmId())));
        Review review1 = reviewStorage.create(review);
        Event event = new Event(review1.getUserId(), EventType.REVIEW, EventOperation.ADD, review1.getReviewId());
        eventService.addEvent(event);
        return reviewStorage.create(review);
    }

    public Review getReview(Long id) {
        return reviewStorage.read(id).orElseThrow(
                () -> new ReviewNotFoundException(String.format("Review with id = %d not found", id)));
    }

    public Review updateReview(Review review) {
        Review updatedReview = reviewStorage.update(review)
                .orElseThrow(() -> new ReviewNotFoundException(String.format("Review with id = %d not found",
                        review.getReviewId())));

        Event event = new Event(updatedReview.getUserId(), EventType.REVIEW, EventOperation.UPDATE, updatedReview.getReviewId());
        eventService.addEvent(event);

        return updatedReview;
    }

    public void deleteReview(Long id) {
        if (!reviewStorage.delete(id)) {
            throw new ReviewNotFoundException(String.format("Review with id = %d not found", id));
        }
    }

    public List<Review> getAllReviews(Long filmId, Integer count) {
        return reviewStorage.getAllReviews(filmId, count);
    }

    public void addLikeToReview(Long reviewId, Long userId) {
        reviewStorage.addLikeToReview(reviewId, userId);
    }

    public void addDislikeToReview(Long reviewId, Long userId) {
        reviewStorage.addDislikeToReview(reviewId, userId);
    }

    public void removeLikeOrDislikeFromReview(Long reviewId, Long userId) {
        reviewStorage.removeLikeOrDislikeFromReview(reviewId, userId);
    }
}
