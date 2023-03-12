package by.tade.taxi.dto;

import by.tade.taxi.yandex.dto.DriverProfileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Value
public class BalanceGridDto {

   private LocalDate startDate;
   private LocalDate endDate;

   private List<BalanceGridItemDto> balanceGridItem;
   private List<DriverProfileDto> allDriverProfiles;

}
