package by.tade.taxi.yandex.service;

import by.tade.taxi.dto.BalanceGridItemDto;
import by.tade.taxi.dto.DiscountGasDto;
import by.tade.taxi.yandex.dto.CreateTransactionDto;
import by.tade.taxi.yandex.dto.DriverProfileGridDto;
import by.tade.taxi.yandex.dto.DriverProfileItemDto;
import by.tade.taxi.yandex.dto.TransactionDto;
import by.tade.taxi.yandex.dto.YandexUserCredentialDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class YandexServiceImpl implements YandexService {

    private final ObjectMapper objectMapper;

    @Override
    public DriverProfileGridDto getDriverProfile(OkHttpClient client, YandexUserCredentialDto userCredential) {

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "{\"query\": {\"park\": {\"id\": \"" + userCredential.getParkId() + "\"}}}");
        Request request = new Request.Builder()
                .url("https://fleet-api.taxi.yandex.net/v1/parks/driver-profiles/list")
                .method("POST", body)
                .addHeader("X-Client-ID", userCredential.getClientId())
                .addHeader("X-Api-Key", userCredential.getApiKey())
                .addHeader("Content-Type", "application/json")
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DriverProfileGridDto driverProfileGrid;
        try {
            String resp = response.body().string();
            driverProfileGrid = objectMapper.readValue(resp, DriverProfileGridDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return driverProfileGrid;
    }

    @Override
    public CreateTransactionDto writeOffMoney(OkHttpClient client, TransactionDto transaction,
                                              YandexUserCredentialDto userCredential, List<DiscountGasDto> discountGas) {
        DriverProfileGridDto driverProfileGridDto = getDriverProfile(client, userCredential);
        Optional<DriverProfileItemDto> driver = findDriverProfile(driverProfileGridDto, transaction.getDriverProfileId());
        CreateTransactionDto createTransactionDto = null;
        BigDecimal summ = transaction.getAmount();
        BigDecimal percent = getDiscountPercent(summ, discountGas);
        BigDecimal summToWriteOfWithDiscount = calculateDiscount(summ, percent);
        transaction.setAmount(summToWriteOfWithDiscount);
        if (driver.isPresent()) {
            // TODO uncomment that work
//            try {
//                createTransactionDto = createTransaction(client, transaction, userCredential);
//            } catch (Exception e) {
            String error = "Пытались списать " + summToWriteOfWithDiscount + ", но сервис Яндекс не ответил. Водитель: " + transaction.getDriverProfileId();
//                log.error(error);
//            throw new RuntimeException(error);
//            }
        } else {
            String error =
                    "Пытались списать " + summToWriteOfWithDiscount + ", но не нашли баланс водителя " + transaction.getDriverProfileId();
            log.error(error);
            throw new RuntimeException(error);
        }
        DriverProfileGridDto driverProfileGridDtoAfterTr = getDriverProfile(client, userCredential);
        Optional<DriverProfileItemDto> driverAfterTr = findDriverProfile(driverProfileGridDtoAfterTr, transaction.getDriverProfileId());
        if (driverAfterTr.get().getAccounts().get(0).getBalance().compareTo(driver.get().getAccounts().get(0).getBalance().negate().subtract(summToWriteOfWithDiscount)) > 0) {
            String info =
                    "Водитель потратил " + summ + ". Cписали " + summToWriteOfWithDiscount + ", с баланса водителя " + transaction.getDriverProfileId();
            log.error(info);
            return new CreateTransactionDto(summToWriteOfWithDiscount, "BY", transaction.getDriverProfileId());
//            return createTransactionDto;
        } else {
            throw new RuntimeException("Не удалось списать деньги пытались списать " + summToWriteOfWithDiscount + " у водителя " + transaction.getDriverProfileId());
        }
    }

    private CreateTransactionDto writeOffMoneyForOneDriver(OkHttpClient client, BalanceGridItemDto balanceGridItem,
                                                           YandexUserCredentialDto userCredential,
                                                           List<DiscountGasDto> discountGas) {
        CreateTransactionDto createTransactionDto = null;
        BigDecimal summ = balanceGridItem.getAmount();
        BigDecimal percent = getDiscountPercent(summ, discountGas);
        BigDecimal summToWriteOfWithDiscount = calculateDiscount(summ, percent);

        if (balanceGridItem.getDriverProfile() != null) {
            // TODO uncomment that work
            try {
                createTransactionDto = createTransaction(client,
                        new TransactionDto(balanceGridItem.getDriverProfile().getId(), summToWriteOfWithDiscount),
                        userCredential);
            } catch (Exception e) {
                String error = "Пытались списать " + summToWriteOfWithDiscount + ", но сервис Яндекс не ответил. Водитель: " + balanceGridItem.getDriverProfile().getId();
                log.error(error);
                throw new RuntimeException(error);
            }
        } else {
            String error =
                    "Пытались списать " + summToWriteOfWithDiscount + ", но не нашли баланс водителя ";
            log.error(error);
            throw new RuntimeException(error);
        }
        return createTransactionDto;
    }

    @Override
    public CreateTransactionDto writeOffMoneyForListDrivers(OkHttpClient client, List<BalanceGridItemDto> balanceGridItems,
                                                            YandexUserCredentialDto userCredential, List<DiscountGasDto> discountGas) {
        List<CreateTransactionDto> createTransactions = balanceGridItems.stream()
                .map(el -> writeOffMoneyForOneDriver(client, el, userCredential, discountGas)).toList();

        return checkAllMoneyIsWriteOff(client, balanceGridItems, createTransactions, userCredential);
    }

    private CreateTransactionDto checkAllMoneyIsWriteOff(OkHttpClient client, List<BalanceGridItemDto> balanceGridItems,
                                                         List<CreateTransactionDto> createTransactions, YandexUserCredentialDto userCredential) {


        DriverProfileGridDto driverProfileGridAfterTr = getDriverProfile(client, userCredential);
        for (int ind = 0; ind < balanceGridItems.size(); ind++) {
            BalanceGridItemDto balanceGridItem = balanceGridItems.get(ind);
            CreateTransactionDto createTransaction = createTransactions.get(ind);
            Optional<DriverProfileItemDto> driverAfterTr = findDriverProfile(driverProfileGridAfterTr,
                    balanceGridItem.getDriverProfile().getId());
//            if (driverAfterTr.get().getAccounts().get(0).getBalance().compareTo(driver.get().getAccounts().get(0).getBalance().negate().subtract(summToWriteOfWithDiscount)) > 0) {
//                String info =
//                        "Водитель потратил "+summ+". Cписали " + summToWriteOfWithDiscount + ", с баланса водителя " + transaction.getDriverProfileId();
//                log.error(info);
//                return new CreateTransactionDto(summToWriteOfWithDiscount, "BY");
////            return createTransactionDto;
//            } else {
//                throw new RuntimeException("Не удалось списать деньги пытались списать " + summToWriteOfWithDiscount + " у водителя " + transaction.getDriverProfileId());
//            }
        }
        return null;
    }

    public BigDecimal getDiscountPercent(BigDecimal amount, List<DiscountGasDto> discountGas) {
        return discountGas.stream()
                .sorted(Comparator.comparing(DiscountGasDto::getSumm).reversed())
                .filter(discountGasDto -> amount.compareTo(discountGasDto.getSumm()) > 0)
                .map(DiscountGasDto::getPercent)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateDiscount(BigDecimal amount, BigDecimal percent) {
        return amount.subtract(percentage(amount, percent));
    }

    private Optional<DriverProfileItemDto> findDriverProfile(DriverProfileGridDto driverProfileGridDto, String driverProfileId) {
        return driverProfileGridDto.getDriverProfiles()
                .stream()
                .filter(dr -> dr.getDriverProfile().getId().equals(driverProfileId))
                .findFirst();
    }

    @Override
    public CreateTransactionDto createTransaction(OkHttpClient client, TransactionDto transaction,
                                                  YandexUserCredentialDto userCredential) {

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "{\r\n  \"amount\": \"-" + transaction.getAmount() + "\",\r\n  " +
                "\"category_id\": \"partner_service_manual\"," +
                "\r\n  \"description\": \"Списание топлива\",\r\n  " +
                "\"driver_profile_id\": \"" + transaction.getDriverProfileId() + "\",\r\n  " +
                "\"park_id\": \"" + userCredential.getParkId() + "\"\r\n}");
        Request request = new Request.Builder()
                .url("https://fleet-api.taxi.yandex.net/v2/parks/driver-profiles/transactions")
                .method("POST", body)
                .addHeader("X-Client-ID", userCredential.getClientId())
                .addHeader("X-API-Key", userCredential.getApiKey())
                .addHeader("X-Idempotency-Token", "c56fa6537e5a4adbbce6ef3593210fb9")
                .addHeader("Content-Type", "text/plain")
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CreateTransactionDto createTransaction;
        try {
            createTransaction = objectMapper.readValue(response.body().string(), CreateTransactionDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return createTransaction;
    }

    public static BigDecimal percentage(BigDecimal base, BigDecimal pct) {
        return base.multiply(pct).divide(new BigDecimal(100));
    }
}
