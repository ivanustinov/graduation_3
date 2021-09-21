package ru.javaops.topjava.to;

import lombok.*;
import ru.javaops.topjava.HasEmail;
import ru.javaops.topjava.util.validation.NoHtml;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTo implements HasEmail {

    @NotBlank
    @Size(min = 2, max = 100)
    @NoHtml
    String name;

    @Email
    @NotBlank
    @Size(max = 100)
    @NoHtml  // https://stackoverflow.com/questions/17480809
    String email;

    @NotBlank
    @Size(min = 5, max = 32)
    String password;


    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
