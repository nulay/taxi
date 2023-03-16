package by.tade.taxi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    private String login;
    private String password;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate endActivationDate;
    private UserSettingsDto settings;
}
