package by.tade.taxi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationDto {
    private String login;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    private String password;
    private String passwordRepeat;
}
