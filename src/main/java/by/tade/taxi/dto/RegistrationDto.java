package by.tade.taxi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationDto {
    public String login;
    public String password;
    public String passwordRepeat;
}
