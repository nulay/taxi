package by.tade.taxi.service;

import by.tade.taxi.dto.RegistrationDto;
import by.tade.taxi.dto.UserLoginDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.dto.UserSettingsDto;

public interface UserService {
    boolean login(UserLoginDto userLogin);

    boolean registration(RegistrationDto registration);

    boolean saveUserSettings(UserSettingsDto userSettings);

    UserSessionDto getUserSession();
}
