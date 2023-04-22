package by.tade.taxi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountGasDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal summ;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal percent;
}
