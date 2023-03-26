package by.tade.taxi.yandex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DriverProfileSimpleGridDto {
    private List<DriverProfileDto> driverProfiles;
}
