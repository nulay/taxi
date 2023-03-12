package by.tade.taxi.beloil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OperationalDto {
    @JsonProperty("cardList")
    private List<CardListDto> cardList;

}
