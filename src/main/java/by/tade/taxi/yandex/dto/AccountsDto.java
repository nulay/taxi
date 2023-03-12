package by.tade.taxi.yandex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountsDto {
    @JsonProperty("balance")
    private String balance;
    @JsonProperty("id")
    private String id;
}
