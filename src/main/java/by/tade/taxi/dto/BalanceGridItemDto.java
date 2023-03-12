package by.tade.taxi.dto;

import by.tade.taxi.yandex.dto.DriverProfileDto;
import by.tade.taxi.yandex.dto.DriverProfileItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BalanceGridItemDto {

    private String fullName;
    private String carNum;
    private String amount;

    private List<DriverProfileDto> driverProfiles;


}
