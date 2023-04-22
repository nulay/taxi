package by.tade.taxi.yandex.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DriverProfileDto {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("id")
    private String id;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("middle_name")
    private String middleName;


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal balance;

    public String getDriverFullName() {
        return (firstName + " " + lastName + " " + middleName).trim();
    }
}
