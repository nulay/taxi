package by.tade.taxi.dto;

import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import by.tade.taxi.yandex.dto.YandexUserCredentialDto;
import lombok.Data;

import java.util.List;

@Data
public class UserSettingsDto {
    private BeloilUserCredentialDto beloilUserCredential;
    private YandexUserCredentialDto yandexUserCredential;

    private WriteOffGasTimeDto writeOffGasTime;
    private List<DiscountGasDto> discountGas;

}
