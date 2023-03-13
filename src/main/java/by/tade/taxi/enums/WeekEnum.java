package by.tade.taxi.enums;

import java.util.Arrays;
import java.util.List;

public enum WeekEnum {
    MONDAY("Понедельник", 1),
    THUESDAY("Вторник", 2),
    WEDNESDAY("Среда", 3),
    THURSDAY("Четверг", 4),
    FRIDAY("Пятница", 5),
    SATURDAY("Суббота", 6),
    SUNDAY("Воскресенье", 7);

    private final String key;
    private final Integer value;

    WeekEnum(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public List<String> getKeys() {
        return Arrays.stream(values()).map(WeekEnum::getKey).toList();
    }

    public Integer getValue() {
        return value;
    }
}
