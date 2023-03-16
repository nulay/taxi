package by.tade.taxi.controller;

import by.tade.taxi.dto.BalanceGridDto;
import by.tade.taxi.dto.BalanceGridItemDto;
import by.tade.taxi.dto.TransactionDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.service.GasBalanceService;
import by.tade.taxi.yandex.dto.CreateTransactionDto;
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

@Controller
@RequiredArgsConstructor
@Slf4j
public class GasBalanceController {

    private final GasBalanceService gasBalanceService;
    private final YandexService yandexService;
    private final UserSessionDto userSession;

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
//        return gasBalanceService.getBalance(userSession, getDate(startDate, 1L),
//                getDate(endDate, 0L));

        return new BalanceGridDto(getDate(startDate, 1L), getDate(endDate, 0L), List.of(new BalanceGridItemDto("hj", "sdf", "345", List.of()),
                new BalanceGridItemDto("fsf", "sf", "400", List.of()),
                new BalanceGridItemDto("ФИО водителя5", "tid5", "Сумма5", List.of())),
                List.of());
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
}
