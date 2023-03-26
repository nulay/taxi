package by.tade.taxi.controller;

import by.tade.taxi.beloil.dto.CardStatusDto;
import by.tade.taxi.beloil.service.BeloilService;
import by.tade.taxi.dto.BalanceGridDto;
import by.tade.taxi.dto.BalanceGridItemDto;
import by.tade.taxi.dto.LinkCardWithYandexProfileRequestDto;
import by.tade.taxi.dto.TransactionDto;
import by.tade.taxi.entity.LinkOilCardToYandexDriverEntity;
import by.tade.taxi.service.GasBalanceService;
import by.tade.taxi.service.UserService;
import by.tade.taxi.yandex.dto.CreateTransactionDto;
import by.tade.taxi.yandex.dto.DriverProfileDto;
import by.tade.taxi.yandex.dto.DriverProfileItemDto;
import by.tade.taxi.yandex.dto.DriverProfileSimpleGridDto;
import by.tade.taxi.yandex.service.YandexService;
import com.squareup.okhttp.OkHttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GasBalanceController {

    private final GasBalanceService gasBalanceService;
    private final YandexService yandexService;
    private final UserService userService;

    private final BeloilService beloilService;

    private static DriverProfileSimpleGridDto driverProfileSimpleGridDto;
    private static List<CardStatusDto> listCard;
    private static BalanceGridDto balanceGridDto;
    private boolean cache = false;

    @GetMapping("/balance-page")
    public String getBalanceView(Model model) {
        model.addAttribute("balanceGrid", new BalanceGridDto(getDate(null, 1L),
                getDate(null, 0L), List.of(), List.of()));
        return "balancegrid";
    }

    @PostMapping("/balance-data")
    @ResponseBody
    public BalanceGridDto getBalance(@RequestParam(value = "startDate", required = false) String startDate,
                                     @RequestParam(value = "endDate", required = false) String endDate) {
        if (balanceGridDto == null && cache) {
            balanceGridDto = gasBalanceService.getBalance(userService.getUserSettings(), getDate(startDate, 1L),
                    getDate(endDate, 0L));
        }
        return balanceGridDto;
//        return new BalanceGridDto(getDate(startDate, 1L), getDate(endDate, 0L), List.of(new BalanceGridItemDto("hj", "sdf", "345", List.of()),
//                new BalanceGridItemDto("fsf", "sf", "400", List.of()),
//                new BalanceGridItemDto("ФИО водителя5", "tid5", "Сумма5", List.of())),
//                List.of());
    }

    @GetMapping("/card-data")
    @ResponseBody
    public List<CardStatusDto> getCardStatuses() {
        if (listCard == null && cache) {
            listCard = beloilService.getCardStatusesWithAuth(userService.getUserSettings().getBeloilUserCredential());

        }else{
            List<LinkOilCardToYandexDriverEntity> linkOilCardToYandexEntities = userService.getLinkCardWithYandexProfile();

            listCard.stream().forEach(card -> card.setYandexId(searchLink(linkOilCardToYandexEntities, card.getCardCode())));

        }
        return listCard;
    }

    // todo remove
    private String searchLink(List<LinkOilCardToYandexDriverEntity> linkOilCardToYandexEntities, Integer cardCode) {
        Optional<LinkOilCardToYandexDriverEntity> linkOilCardToYandexO = linkOilCardToYandexEntities.stream()
                .filter(link -> link.getCardCode().equals(cardCode.toString())).findFirst();
        if(linkOilCardToYandexO.isPresent()){
            return linkOilCardToYandexO.get().getYandexDriverProfile();
        }
        return null;
    }

    @GetMapping("/card-page")
    public String getCardPage() {
        return "cardMatching";
    }

    private static LocalDate getDate(String date, long minusDays) {
        if (date == null) {
            return LocalDate.now().minusDays(minusDays);
        }
        return LocalDate.parse(date);
    }


    @PostMapping("/transaction")
    @ResponseBody
    public CreateTransactionDto transaction(@RequestBody TransactionDto transaction) {
        OkHttpClient okHttpClient = new OkHttpClient();
//        return yandexService.createTransaction(okHttpClient, transaction, userSession.getSettings().getYandexUserCredential());
        return new CreateTransactionDto(transaction.getAmount(), "BY");
    }

    @GetMapping("/driver-profile-data")
    @ResponseBody
    public DriverProfileSimpleGridDto getDriverProfileGrid() {
        if (driverProfileSimpleGridDto == null && cache) {
            driverProfileSimpleGridDto = new DriverProfileSimpleGridDto(yandexService.getDriverProfile(new OkHttpClient(),
                            userService.getUserSettings().getYandexUserCredential()).getDriverProfiles().stream()
                    .map(driverProfileItem -> {
                        DriverProfileDto driverProfile = driverProfileItem.getDriverProfile();
                        driverProfile.setBalance(driverProfileItem.getAccounts().get(0).getBalance());
                        return driverProfile;
                    })
                    .toList());
        }
        return driverProfileSimpleGridDto;
    }

    @PostMapping("/link-card-yandex-profile")
    @ResponseBody
    public boolean linkCardWithYandexProfile(@RequestBody LinkCardWithYandexProfileRequestDto linkCardWithYandexProfileRequest) {
        userService.linkCardWithYandexProfile(linkCardWithYandexProfileRequest);
        return true;
    }

    @PostMapping("/delete_link-card-yandex-profile")
    @ResponseBody
    public boolean deleteLinkCardWithYandexProfile(@RequestBody LinkCardWithYandexProfileRequestDto linkCardWithYandexProfileRequest) {
        userService.deleteLinkCardWithYandexProfile(linkCardWithYandexProfileRequest);
        return true;
    }
}
