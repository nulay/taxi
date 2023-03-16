package by.tade.taxi.enums;

import java.util.Arrays;
import java.util.List;

public enum WeekEnum {
    MONDAY("Понедельник", "MON"),
    THUESDAY("Вторник", "TUE"),
    WEDNESDAY("Среда", "WED"),
    THURSDAY("Четверг", "THU"),
    FRIDAY("Пятница", "FRI"),
    SATURDAY("Суббота", "SAT"),
    SUNDAY("Воскресенье", "SUN");

    private final String key;
    private final String value;

    WeekEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public List<String> getKeys() {
        return Arrays.stream(values()).map(WeekEnum::getKey).toList();
    }

    public String getValue() {
        return value;
    }
}
