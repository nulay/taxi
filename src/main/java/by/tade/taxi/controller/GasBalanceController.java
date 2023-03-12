package by.tade.taxi.controller;

import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import by.tade.taxi.dto.TransactionDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.service.GasBalanceService;
import by.tade.taxi.yandex.dto.CreateTransactionDto;
import by.tade.taxi.yandex.dto.YandexUserCredentialDto;
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

@Controller
@RequiredArgsConstructor
@Slf4j
public class GasBalanceController {

    private final GasBalanceService gasBalanceService;
    private final YandexService yandexService;
    private final UserSessionDto userSession;

    @GetMapping("/balance")
    public String getBalance(Model model, @RequestParam(value = "startDate",required = false) String startDate,
                             @RequestParam(value = "endDate", required = false) String endDate) {

        model.addAttribute("balanceGrid", gasBalanceService.getBalance(userSession, getDate(startDate, 1L),
                getDate(endDate, 0L)));

        return "balancegrid";
    }

    private static LocalDate getDate(String date, long minusDays) {
        if (date == null){
            return LocalDate.now().minusDays(minusDays);
        }
        return LocalDate.parse(date);
    }


    @PostMapping("/transaction")
    @ResponseBody
    public CreateTransactionDto transaction(@RequestBody TransactionDto transaction) {
        OkHttpClient okHttpClient = new OkHttpClient();
        return yandexService.createTransaction(okHttpClient, transaction, userSession.getYandexUserCredential());

    }
}
