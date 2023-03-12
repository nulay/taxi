package by.tade.taxi.dto;

import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import by.tade.taxi.yandex.dto.YandexUserCredentialDto;
import lombok.Data;

@Data
public class UserDto {
    public String login;
    public String password;
    private BeloilUserCredentialDto beloilUserCredential;
    private YandexUserCredentialDto yandexUserCredential;
}
