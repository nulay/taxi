package by.tade.taxi.dto;

import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import by.tade.taxi.yandex.dto.YandexUserCredentialDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSessionDto {
    private BeloilUserCredentialDto beloilUserCredential;
    private YandexUserCredentialDto yandexUserCredential;
}
