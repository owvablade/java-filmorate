package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validation.annotations.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Film {

    private Long id;
    @NotBlank(message = "Name must not be blank")
    private String name;
    @Length(max = 200, message = "Description length must be less than 200")
    private String description;
    @MinimumDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();
    @Setter(AccessLevel.NONE)
    private Set<Long> likes = new HashSet<>();

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void deleteLike(Long userId) {
        likes.remove(userId);
    }

    public boolean containsLike(Long userId) {
        return likes.contains(userId);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void deleteGenre(Genre genre) {
        genres.remove(genre);
    }

    public boolean containsGenre(Genre genre) {
        return genres.contains(genre);
    }

    public int getLikesCount() {
        return likes.size();
    }
}
