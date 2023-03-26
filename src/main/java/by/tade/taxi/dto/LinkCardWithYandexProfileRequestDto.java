package by.tade.taxi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class LinkCardWithYandexProfileRequestDto {
    private String cardCode;

    private String yandexDriverProfile;
}
