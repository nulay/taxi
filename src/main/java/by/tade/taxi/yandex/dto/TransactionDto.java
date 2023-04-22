package by.tade.taxi.yandex.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    private String driverProfileId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;
}
