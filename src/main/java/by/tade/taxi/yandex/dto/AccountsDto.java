package by.tade.taxi.yandex.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountsDto {

    @JsonProperty("balance")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal balance;

    @JsonProperty("id")
    private String id;
}
