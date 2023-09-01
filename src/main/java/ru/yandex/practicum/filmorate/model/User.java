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
import java.util.Set;

@Data
@Accessors(chain = true)
public class User {

    private Long id;
    @Email(message = "Invalid email")
    @NotBlank(message = "Email must not be blank")
    private String email;
    @Setter(AccessLevel.NONE)
    @NotBlank(message = "Login must not be blank")
    @Pattern(regexp = "^\\S+$", message = "Login must not contain whitespaces")
    private String login;
    private String name;
    @Past(message = "Date of birth must be earlier than today")
    private LocalDate birthday;
    @Setter(AccessLevel.NONE)
    private Set<Long> friends;

    public User setLogin(String login) {
        this.login = login;
        if (this.name == null) {
            this.name = login;
        }
        return this;
    }

    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }

    public boolean containsFriend(Long friendId) {
        return friends.contains(friendId);
    }
}
