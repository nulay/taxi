package by.tade.taxi.beloil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TotalDto {
    @JsonProperty("costTotalWithVAT")
    private String costTotalWithVAT;
}
