package by.tade.taxi.service;

import by.tade.taxi.dto.BalanceGridDto;
import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import by.tade.taxi.dto.UserSessionDto;

import java.time.LocalDate;

public interface GasBalanceService {
    BalanceGridDto getBalance(UserSessionDto serSession, LocalDate startDate, LocalDate endDate);
}
