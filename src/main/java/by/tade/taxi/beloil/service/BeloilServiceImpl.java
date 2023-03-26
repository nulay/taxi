package by.tade.taxi.beloil.service;

import by.tade.taxi.beloil.dto.AccessDto;
import by.tade.taxi.beloil.dto.CardStatusDto;
import by.tade.taxi.beloil.dto.OperationalDto;
import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import by.tade.taxi.entity.LinkOilCardToYandexDriverEntity;
import by.tade.taxi.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BeloilServiceImpl implements BeloilService {

    private final ObjectMapper objectMapper;
    private final UserService userService;
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
        if(HttpStatus.OK.value() !=  response.code()){
            throw new RuntimeException("Beloil service is broken");
        }

        OperationalDto operational;
        try {
            operational = objectMapper.readValue(response.body().string(), OperationalDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return operational;
    }

    @Override
    public boolean changeCardValues(OkHttpClient client, AccessDto access) {

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "[{" +
                "\"contrCode\": 6800606," +
                "\"cardCode\": 650183695," +
                "\"monthNorm\": 6169," +
                "\"dayNorm\": 199," +
                "\"dayNormAmount\": 350," +
                "\"oilGroupSet\": [" +
                "{" +
                "\"code\": 1," +
                "\"name\": \"ДТ\"" +
                "}]," +
                "\"transitFl\": true," +
                "\"goodsFl\": true," +
                "\"transitGoodsFl\": true," +
                "\"status\": 1," +
                "\"actionDate\": \"2070-01-01T00:00:00\"," +
                "\"division\": 0," +
                "\"driver\": \"\"," +
                "\"carNum\": \"\"," +
                "\"priority\": 0," +
                "\"dosePermitted\": 100," +
                "\"dosePermittedAmount\": 175," +
                "\"kapschCard\": false," +
                "\"kapschContract\": false}]");
        Request request = new Request.Builder()
                .url("https://ssl.beloil.by/rcp/i/api/v2/Contract/cards")
                .method("PUT", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + access.getAccessToken())
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(HttpStatus.OK.value() !=  response.code()){
            throw new RuntimeException("Beloil service is broken");
        }
        return true;
    }

    @Override
    public List<CardStatusDto> getCardStatusesWithAuth(BeloilUserCredentialDto userCredential) {
        OkHttpClient client = new OkHttpClient();
        AccessDto access = getAccessToGas(client, userCredential);
        return getCardStatuses(client, access);
    }

    @Override
    public List<CardStatusDto> getCardStatuses(OkHttpClient client, AccessDto access) {

        MediaType mediaType = MediaType.parse("application/json");

        Request request = new Request.Builder()
                .url("https://ssl.beloil.by/rcp/i/api/v2/Contract/cards")
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + access.getAccessToken())
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(HttpStatus.OK.value() !=  response.code()){
            throw new RuntimeException("Beloil service is broken");
        }
        List<CardStatusDto> cardStatuses;
        try {
            cardStatuses = objectMapper.readValue(response.body().string(), new TypeReference<List<CardStatusDto>>(){});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<LinkOilCardToYandexDriverEntity> linkOilCardToYandexEntities = userService.getLinkCardWithYandexProfile();

        cardStatuses.stream().forEach(card -> card.setYandexId(searchLink(linkOilCardToYandexEntities, card.getCardCode())));
        return cardStatuses;
    }

    private String searchLink(List<LinkOilCardToYandexDriverEntity> linkOilCardToYandexEntities, Integer cardCode) {
        Optional<LinkOilCardToYandexDriverEntity> linkOilCardToYandexO = linkOilCardToYandexEntities.stream()
                .filter(link -> link.getCardCode().equals(cardCode.toString())).findFirst();
        if(linkOilCardToYandexO.isPresent()){
            return linkOilCardToYandexO.get().getYandexDriverProfile();
        }
        return null;
    }
}
