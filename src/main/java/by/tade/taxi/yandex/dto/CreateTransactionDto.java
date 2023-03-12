package by.tade.taxi.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateTransactionDto {
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("currency_code")
    private String currencyCode;
}
