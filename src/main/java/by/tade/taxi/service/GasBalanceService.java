package by.tade.taxi.service;

import by.tade.taxi.dto.BalanceGridDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.dto.UserSettingsDto;

import java.time.LocalDate;

public interface GasBalanceService {
    BalanceGridDto getBalance(UserSettingsDto userSettings, LocalDate startDate, LocalDate endDate);
}
