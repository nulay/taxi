package by.tade.taxi.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DriverProfileGridDto {
    @JsonProperty("driver_profiles")
    private List<DriverProfileItemDto> driverProfiles;

}
