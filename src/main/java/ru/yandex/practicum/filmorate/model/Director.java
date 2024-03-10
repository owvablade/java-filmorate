package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Director {

    private final int id;

    @NotBlank(message = "Name of director must not be blank")
    private final String name;
}
