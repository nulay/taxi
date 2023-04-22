package by.tade.taxi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    private Integer id;
    private String login;
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endActivationDate;
    private UserSettingsDto settings;
}
