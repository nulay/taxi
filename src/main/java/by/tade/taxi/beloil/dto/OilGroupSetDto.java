package by.tade.taxi.beloil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OilGroupSetDto {
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("name")
    private String name;
}
