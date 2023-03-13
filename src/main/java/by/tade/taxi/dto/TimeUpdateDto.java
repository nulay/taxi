package by.tade.taxi.dto;

import by.tade.taxi.enums.WeekEnum;
import lombok.Data;

@Data
public class TimeUpdateDto {
    private WeekEnum weekEnum;

    private boolean day;

    private boolean week;
    private String weekDay;

    private boolean monthly;
    private String monthDay;
}
