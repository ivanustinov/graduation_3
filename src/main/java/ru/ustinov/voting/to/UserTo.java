package ru.ustinov.voting.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ustinov.voting.HasEmail;
import ru.ustinov.voting.util.validation.NoHtml;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
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
