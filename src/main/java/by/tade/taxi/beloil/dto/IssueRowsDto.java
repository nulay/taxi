package by.tade.taxi.beloil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IssueRowsDto {
    @JsonProperty("carNum")
    private String carNum;
    @JsonProperty("driverName")
    private String driverName;
    @JsonProperty("costTotalWithVAT")
    private String costTotalWithVAT;
}
