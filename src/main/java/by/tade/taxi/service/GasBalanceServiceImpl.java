package by.tade.taxi.service;

import by.tade.taxi.beloil.dto.AccessDto;
import by.tade.taxi.beloil.dto.IssueRowsDto;
import by.tade.taxi.beloil.dto.OperationalDto;
import by.tade.taxi.beloil.service.BeloilService;
import by.tade.taxi.dto.BalanceGridDto;
import by.tade.taxi.dto.BalanceGridItemDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.dto.UserSettingsDto;
import by.tade.taxi.entity.LinkOilCardToYandexDriverEntity;
import by.tade.taxi.entity.repository.LinkOilCardToYandexRepository;
import by.tade.taxi.yandex.dto.AccountsDto;
import by.tade.taxi.yandex.dto.DriverProfileDto;
import by.tade.taxi.yandex.dto.DriverProfileGridDto;
import by.tade.taxi.yandex.dto.DriverProfileItemDto;
import by.tade.taxi.yandex.service.YandexServiceImpl;
import com.squareup.okhttp.OkHttpClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class GasBalanceServiceImpl implements GasBalanceService {

    private final BeloilService beloilService;
    private final YandexServiceImpl yandexServiceImpl;

    private final LinkOilCardToYandexRepository linkOilCardToYandexRepository;

    @Override
    public BalanceGridDto getBalance(UserSessionDto userSession, UserSettingsDto userSettings, LocalDate startDate, LocalDate endDate) {

        OkHttpClient client = new OkHttpClient();
        AccessDto access = beloilService.getAccessToGas(client, userSettings.getBeloilUserCredential());
        OperationalDto operationalDto = beloilService.getBalance(client, userSettings.getBeloilUserCredential(), access, startDate, endDate);
        DriverProfileGridDto driverProfileGrid = getYandexDriverProfileGrid(userSettings);
        List<LinkOilCardToYandexDriverEntity> linkOilCardToYandex =
                linkOilCardToYandexRepository.getAllByUserTaxiId(userSession.getIdUser());
        List<BalanceGridItemDto> balanceGridItems = operationalDto.getCardList().stream().map(card -> {
            DriverProfileDto driverProfile = tryToFindYandexDriver(driverProfileGrid, linkOilCardToYandex,
                    card.getCardNumber());
            return new BalanceGridItemDto(card.getCardNumber(), card.getTotal().getCostTotalWithVAT(),
                    driverProfile);
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

    private DriverProfileDto tryToFindYandexDriver(DriverProfileGridDto driverProfileGrid, List<LinkOilCardToYandexDriverEntity> linkOilCardToYandex, String cardNum) {
        if (driverProfileGrid == null) {
            return null;
        }
        Optional<LinkOilCardToYandexDriverEntity> driverLink = linkOilCardToYandex.stream().filter(card ->
                        card.getCardCode().equals(cardNum))
                .findFirst();

        if(driverLink.isPresent()) {
            Optional<DriverProfileDto> driverO = driverProfileGrid.getDriverProfiles().stream().filter(drProf ->
                            drProf.getDriverProfile().getId().equals(driverLink.get().getYandexDriverProfile()))
                    .map(DriverProfileItemDto::getDriverProfile)
                    .findFirst();
            if(driverO.isPresent()) {
                return driverO.get();
            }else{
                log.error("We can not to find driver with {}", driverLink.get().getYandexDriverProfile());
                // todo удалить такого водителя из таблицы линков
                // linkOilCardToYandexRepository.delete(driverLink.get());
            }
        }
        return null;
    }

    private List<DriverProfileDto> tryToFindYandexDriverOld(DriverProfileGridDto driverProfileGrid, String driverName, String carNum) {
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

    private DriverProfileGridDto getYandexDriverProfileGrid(UserSettingsDto userSettings) {
        OkHttpClient client = new OkHttpClient();
        return yandexServiceImpl.getDriverProfile(client, userSettings.getYandexUserCredential());
    }
}
