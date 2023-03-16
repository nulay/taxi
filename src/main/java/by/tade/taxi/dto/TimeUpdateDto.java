package by.tade.taxi.dto;

import by.tade.taxi.enums.WeekEnum;
import lombok.Data;

@Data
public class TimeUpdateDto {
    public static final String DAY = "day";
    public static final String MONTH = "month";
    public static final String WEEK = "week";

    private WeekEnum weekEnum;

    private String scheduler;

    private String monthDay;
}
