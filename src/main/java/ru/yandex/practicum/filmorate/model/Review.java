package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Review {

    private long reviewId;

    @NotBlank(message = "Content must not be blank")
    private final String content;

    @Getter(AccessLevel.NONE)
    @NotNull
    private final Boolean isPositive;

    @NotNull
    private final Long userId;

    @NotNull
    private final Long filmId;

    private int useful;

    @JsonProperty(value = "isPositive")
    public boolean isPositive() {
        return isPositive;
    }
}
