package by.tade.taxi.dto;

import lombok.Data;

@Data
public class UserDto {
    public String login;
    public String password;
    private UserSettingsDto settings;
}
