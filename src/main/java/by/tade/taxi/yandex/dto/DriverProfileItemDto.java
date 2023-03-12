package by.tade.taxi.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DriverProfileItemDto {
    @JsonProperty("driver_profile")
    private DriverProfileDto driverProfile;
    @JsonProperty("accounts")
    private List<AccountsDto> accounts;
}
