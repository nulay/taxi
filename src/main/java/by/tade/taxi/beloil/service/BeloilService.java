package by.tade.taxi.beloil.service;

import by.tade.taxi.beloil.dto.AccessDto;
import by.tade.taxi.beloil.dto.CardStatusDto;
import by.tade.taxi.beloil.dto.OperationalDto;
import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import com.squareup.okhttp.OkHttpClient;

import java.time.LocalDate;
import java.util.List;

public interface BeloilService {
    AccessDto getAccessToGas(OkHttpClient client, BeloilUserCredentialDto userCredential);

    OperationalDto getBalance(OkHttpClient client, BeloilUserCredentialDto userCredential, AccessDto access, LocalDate startDate, LocalDate endDate);

    boolean changeCardValues(OkHttpClient client, AccessDto access);

    List<CardStatusDto> getCardStatuses(OkHttpClient client, AccessDto access);

    List<CardStatusDto> getCardStatusesWithAuth(BeloilUserCredentialDto userCredential);
}
