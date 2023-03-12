package by.tade.taxi.beloil.service;

import by.tade.taxi.beloil.dto.AccessDto;
import by.tade.taxi.beloil.dto.OperationalDto;
import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class BeloilServiceImpl implements BeloilService {

    private final ObjectMapper objectMapper;
    private final DateTimeFormatter beloilDateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @Override
    public AccessDto getAccessToGas(OkHttpClient client, BeloilUserCredentialDto userCredential) {

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=password" +
                "&client_id=rcp.web" +
                "&client_secret=secret" +
                "&scope=rcp.api" +
                "&username=" + userCredential.getEmitentId() + "." + userCredential.getContractId() +
                "&password=" + userCredential.getPassword() +
                "&emitentId=" + userCredential.getEmitentId() +
                "&contractId=" + userCredential.getContractId());
        Request request = new Request.Builder()
                .url("https://belorusneft.by/identity/connect/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AccessDto access;
        try {
            access = objectMapper.readValue(response.body().string(), AccessDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return access;
    }

    @Override
    public OperationalDto getBalance(OkHttpClient client, BeloilUserCredentialDto userCredential, AccessDto access, LocalDate startDate, LocalDate endDate) {
        MediaType mediaType = MediaType.parse("application/json");

        String dataStart = beloilDateFormatter.format(startDate);
        String dataToday = beloilDateFormatter.format(endDate);
        RequestBody body = RequestBody.create(mediaType, "{" +
                "\"startDate\": \"" + dataStart + "\"," +
                "\"endDate\": \"" + dataToday + "\"," +
                "\"cardNumber\": 0," +
                "\"subDivisnNumber\": -1," +
                "\"flChoice\": 1}");
        Request request = new Request.Builder()
                .url("https://ssl.beloil.by/rcp/i/api/v2/Contract/operational")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + access.getAccessToken())
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        OperationalDto operational;
        try {
            operational = objectMapper.readValue(response.body().string(), OperationalDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return operational;
    }
}
