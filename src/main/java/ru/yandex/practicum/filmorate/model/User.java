package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class User {

    private Long id;
    @Email
    @NotBlank
    private String email;
    @Setter(AccessLevel.NONE)
    @NotBlank
    @Pattern(regexp = "^\\S+$", message = "Login must not contain whitespaces")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;

    public User setLogin(String login) {
        this.login = login;
        if (this.name == null) {
            this.name = login;
        }
        return this;
    }
}
