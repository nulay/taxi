package by.tade.taxi.yandex.service;

import by.tade.taxi.dto.BalanceGridItemDto;
import by.tade.taxi.dto.DiscountGasDto;
import by.tade.taxi.yandex.dto.TransactionDto;
import by.tade.taxi.yandex.dto.CreateTransactionDto;
import by.tade.taxi.yandex.dto.DriverProfileGridDto;
import by.tade.taxi.yandex.dto.YandexUserCredentialDto;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

public interface YandexService {
    DriverProfileGridDto getDriverProfile(OkHttpClient client, YandexUserCredentialDto userCredential);

    CreateTransactionDto createTransaction(OkHttpClient client, TransactionDto transaction,
                                           YandexUserCredentialDto userCredential);

    CreateTransactionDto writeOffMoney(OkHttpClient client, TransactionDto transaction,
                                           YandexUserCredentialDto userCredential, List<DiscountGasDto> discountGas);

    CreateTransactionDto writeOffMoneyForListDrivers(OkHttpClient client, List<BalanceGridItemDto> balanceGridItems,
                                                     YandexUserCredentialDto userCredential, List<DiscountGasDto> discountGas);
}
