package by.tade.taxi.beloil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CardListDto {
    @JsonProperty("cardNumber")
    private String cardNumber;
    @JsonProperty("issueRows")
    private List<IssueRowsDto> issueRows;
    @JsonProperty("total")
    private TotalDto total;
}
