package by.tade.taxi.beloil.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TotalDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("costTotalWithVAT")
    private BigDecimal costTotalWithVAT;
}
