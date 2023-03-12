package by.tade.taxi.service;

import by.tade.taxi.beloil.dto.AccessDto;
import by.tade.taxi.beloil.dto.IssueRowsDto;
import by.tade.taxi.beloil.dto.OperationalDto;
import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import by.tade.taxi.beloil.service.BeloilService;
import by.tade.taxi.dto.BalanceGridDto;
import by.tade.taxi.dto.BalanceGridItemDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.yandex.dto.AccountsDto;
import by.tade.taxi.yandex.dto.DriverProfileDto;
import by.tade.taxi.yandex.dto.DriverProfileGridDto;
import by.tade.taxi.yandex.dto.DriverProfileItemDto;
import by.tade.taxi.yandex.dto.YandexUserCredentialDto;
import by.tade.taxi.yandex.service.YandexServiceImpl;
import com.squareup.okhttp.OkHttpClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class GasBalanceServiceImpl implements GasBalanceService {

    private final BeloilService beloilService;
    private final YandexServiceImpl yandexServiceImpl;

    @Override
    public BalanceGridDto getBalance(UserSessionDto userSession, LocalDate startDate, LocalDate endDate) {

        OkHttpClient client = new OkHttpClient();
        AccessDto access = beloilService.getAccessToGas(client, userSession.getBeloilUserCredential());
        OperationalDto operationalDto = beloilService.getBalance(client, userSession.getBeloilUserCredential(), access, startDate, endDate);
        DriverProfileGridDto driverProfileGrid = getYandexDriverProfileGrid(userSession);
        List<BalanceGridItemDto> balanceGridItems = operationalDto.getCardList().stream().map(card -> {
            IssueRowsDto issueRow = card.getIssueRows().stream().findFirst().get();
            List<DriverProfileDto> driverProfiles = tryToFindYandexDriver(driverProfileGrid, issueRow.getDriverName(),
                    issueRow.getCarNum());
            return new BalanceGridItemDto(issueRow.getDriverName(), issueRow.getCarNum(), card.getTotal().getCostTotalWithVAT(),
                    driverProfiles);
        }).toList();
        return new BalanceGridDto(startDate, endDate, balanceGridItems, getAllDriverProfiles(driverProfileGrid));
    }

    private static List<DriverProfileDto> getAllDriverProfiles(DriverProfileGridDto driverProfileGrid) {
        return driverProfileGrid.getDriverProfiles().stream()
                .map(drPr -> {
                    DriverProfileDto driverProfile = drPr.getDriverProfile();
                    if (drPr.getAccounts().size() > 0) {
                        AccountsDto accounts = drPr.getAccounts().get(0);
                        driverProfile.setBalance(accounts.getBalance());
                    }
                    return driverProfile;
                })
                .sorted(Comparator.comparing(DriverProfileDto::getLastName))
                .toList();
    }

    private List<DriverProfileDto> tryToFindYandexDriver(DriverProfileGridDto driverProfileGrid, String driverName, String carNum) {
        if (driverProfileGrid == null) {
            return new ArrayList<>();
        }
        List<DriverProfileDto> itemsDriver = driverProfileGrid.getDriverProfiles().stream().filter(drProf ->
                driverName.toLowerCase().startsWith(drProf.getDriverProfile().getLastName().toLowerCase()) ||
                        drProf.getDriverProfile().getId().equals(carNum))
                .map(DriverProfileItemDto::getDriverProfile)
                .toList();
        return itemsDriver;
    }

    private DriverProfileGridDto getYandexDriverProfileGrid(UserSessionDto userSession) {
        OkHttpClient client = new OkHttpClient();
        return yandexServiceImpl.getDriverProfile(client, userSession.getYandexUserCredential());
    }
}
