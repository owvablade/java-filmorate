package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        log.info("Invoke post review method at resource POST /reviews={}", review);
        return reviewService.createdReview(review);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Long id) {
        log.info("Invoke get review method at resource GET /reviews with id={}", id);
        return reviewService.getReview(id);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review newReview) {
        log.info("Invoke update review method at resource PUT /reviews={}", newReview);
        return reviewService.updateReview(newReview);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info("Invoke delete review method at resource DELETE /reviews with id={}", id);
        reviewService.deleteReview(id);
    }

    @GetMapping
    public List<Review> getAllReviews(@RequestParam(required = false) Long filmId,
                                      @RequestParam(defaultValue = "10") Integer count) {
        log.info("Invoke get review method at resource GET /reviews with filmId={} and count={}", filmId, count);
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Invoke add like to review method at resource PUT /{}/like/{} ", id, userId);
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Invoke add dislike to review method at resource PUT /{}/dislike/{} ", id, userId);
        reviewService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Invoke delete dislike from review method at resource DELETE /{}/like/{} ", id, userId);
        reviewService.removeLikeOrDislikeFromReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Invoke delete dislike from review method at resource DELETE /{}/dislike/{} ", id, userId);
        reviewService.removeLikeOrDislikeFromReview(id, userId);
    }
}
