package by.tade.taxi.service;

import by.tade.taxi.dto.LinkCardWithYandexProfileRequestDto;
import by.tade.taxi.dto.RegistrationDto;
import by.tade.taxi.dto.UserLoginDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.dto.UserSettingsDto;
import by.tade.taxi.dto.UserStorageDto;
import by.tade.taxi.entity.LinkOilCardToYandexDriverEntity;

import java.util.List;

public interface UserService {
    boolean login(UserLoginDto userLogin);

    boolean registration(RegistrationDto registration);

    boolean saveUserSettings(UserSettingsDto userSettings);

    UserSessionDto getUserSession();

    UserSettingsDto getUserSettings();

    UserStorageDto loadAllUsers();

    void linkCardWithYandexProfile(LinkCardWithYandexProfileRequestDto linkCardWithYandexProfileRequest);

    List<LinkOilCardToYandexDriverEntity> getLinkCardWithYandexProfile();

    void deleteLinkCardWithYandexProfile(LinkCardWithYandexProfileRequestDto linkCardWithYandexProfileRequest);
}
