package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.json.deserializers.DurationDeserializer;
import ru.yandex.practicum.filmorate.json.serializers.DurationSerializer;
import ru.yandex.practicum.filmorate.validation.annotations.MinimumDate;
import ru.yandex.practicum.filmorate.validation.annotations.PositiveDuration;

import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Film {

    private Long id;
    @NotBlank
    private String name;
    @Length(max = 200, message = "Description length must be less than 200")
    private String description;
    @MinimumDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @PositiveDuration
    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;
}
