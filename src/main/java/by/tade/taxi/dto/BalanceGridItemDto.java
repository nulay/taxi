package by.tade.taxi.dto;

import by.tade.taxi.yandex.dto.DriverProfileDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceGridItemDto {

    private String cardNum;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;

    private DriverProfileDto driverProfile;


}
