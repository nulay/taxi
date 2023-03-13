package by.tade.taxi.dto;

import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import by.tade.taxi.yandex.dto.YandexUserCredentialDto;
import lombok.Data;

@Data
public class UserSettingsDto {
    private BeloilUserCredentialDto beloilUserCredential;
    private YandexUserCredentialDto yandexUserCredential;

    private TimeUpdateDto timeUpdate;

}
