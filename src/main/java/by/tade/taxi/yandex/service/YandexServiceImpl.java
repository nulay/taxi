package by.tade.taxi.yandex.service;

import by.tade.taxi.dto.BalanceGridItemDto;
import by.tade.taxi.dto.TransactionDto;
import by.tade.taxi.yandex.dto.CreateTransactionDto;
import by.tade.taxi.yandex.dto.DriverProfileGridDto;
import by.tade.taxi.yandex.dto.YandexUserCredentialDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
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
    public CreateTransactionDto createTransaction(OkHttpClient client, TransactionDto transaction,
                                                  YandexUserCredentialDto userCredential) {

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "{\r\n  \"amount\": \"-" + transaction.getAmount() + "\",\r\n  " +
                "\"category_id\": \"partner_service_manual\"," +
                "\r\n  \"description\": \"Списание топлива\",\r\n  " +
                "\"driver_profile_id\": \"" + transaction.getId() + "\",\r\n  " +
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
}
