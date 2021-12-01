package ru.ustinov.voting.web;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;
import ru.ustinov.voting.model.User;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString(of = "user")
public class AuthUser extends org.springframework.security.core.userdetails.User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private User user;

    public AuthUser(@NonNull User user) {
        super(user.getEmail(), user.getPassword(), user.getRoles());
        this.user = user;
    }

    public int id() {
        return user.id();
    }

}