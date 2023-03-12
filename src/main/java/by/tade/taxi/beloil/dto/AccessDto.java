package by.tade.taxi.beloil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccessDto {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private String expressIn;

    @JsonProperty("token_type")
    private String tokenType;

    private String scope;
}
