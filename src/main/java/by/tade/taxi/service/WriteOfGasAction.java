package by.tade.taxi.service;

import by.tade.taxi.dto.BalanceGridDto;
import by.tade.taxi.dto.BalanceGridItemDto;
import by.tade.taxi.dto.UserDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.dto.WriteOffGasTimeDto;
import by.tade.taxi.entity.repository.LinkOilCardToYandexRepository;
import by.tade.taxi.yandex.service.YandexServiceImpl;
import com.squareup.okhttp.OkHttpClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

@Data
@AllArgsConstructor
@Slf4j
public class WriteOfGasAction {

    private final UserDto user;
    private final GasBalanceService gasBalanceService;
    private final LinkOilCardToYandexRepository linkOilCardToYandexRepository;
    private final YandexServiceImpl yandexServiceImpl;
    private final UserService userService;

    public void execute() {
        if (user.getEndActivationDate().isBefore(LocalDate.now())) {
            log.error("User activation is finished");
            throw new RuntimeException("User activation is finished");
        }
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now().minusDays(1L);
        if (user.getSettings().getWriteOffGasTime().getScheduler().equals(WriteOffGasTimeDto.DAY)) {
            startDate = LocalDate.now().minusDays(2L);
        }
        if (user.getSettings().getWriteOffGasTime().getScheduler().equals(WriteOffGasTimeDto.WEEK)) {
            startDate = LocalDate.now().minusDays(8L);
        }
        if (user.getSettings().getWriteOffGasTime().getScheduler().equals(WriteOffGasTimeDto.DAY)) {
            startDate = LocalDate.now().minusMonths(1L).minusDays(1L);
        }
        if (startDate == null){
            return;
        }
        BalanceGridDto balanceGrid = gasBalanceService.getBalance(new UserSessionDto(user.getId(), user.getLogin()),
                userService.getUserSettings(user.getLogin()),
                startDate,
                endDate);

        List<BalanceGridItemDto> balanceGridItems = balanceGrid.getBalanceGridItem().stream()
                .filter(drPr -> drPr.getDriverProfile() != null && drPr.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        // todo нашли связанные балансы - теперь нужно списать с каждого средства

        yandexServiceImpl.writeOffMoneyForListDrivers(new OkHttpClient(), balanceGridItems, user.getSettings().getYandexUserCredential(),
                user.getSettings().getDiscountGas());
    }

}
