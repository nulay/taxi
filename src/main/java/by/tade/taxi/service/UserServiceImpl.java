package by.tade.taxi.service;

import by.tade.taxi.dto.RegistrationDto;
import by.tade.taxi.dto.UserDto;
import by.tade.taxi.dto.UserLoginDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.dto.UserSettingsDto;
import by.tade.taxi.dto.UserStorageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.logging.log4j.LoggingException;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Optional;

@Service
@Data
public class UserServiceImpl implements UserService {

    private final ObjectMapper objectMapper;

    @Resource(name = "userSession")
    UserSessionDto userSession;

    @Override
    public boolean login(UserLoginDto userLogin) {
        UserStorageDto userStorage = null;
        try {
            userStorage = loadUser();
        } catch (IOException e) {
            throw new RuntimeException("User not found");
        }
        Optional<UserDto> userO = userStorage.getUsers().stream()
                .filter(userLoginIn -> userLogin.getLogin().equals(userLoginIn.getLogin()) &&
                        userLogin.getPassword().equals(userLoginIn.getPassword())).findFirst();
        if (userO.isPresent()) {
            UserDto user = userO.get();
            userSession.getSettings().setBeloilUserCredential(user.getSettings().getBeloilUserCredential());
            userSession.getSettings().setYandexUserCredential(user.getSettings().getYandexUserCredential());
            userSession.setLogin(user.getLogin());
            return true;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public boolean registration(RegistrationDto registration) {
        if (!registration.getPassword().equals(registration.getPasswordRepeat())) {
            throw new LoggingException("Password and repeat password don't match");
        }
        try {
            UserStorageDto userStorage = loadUser();
            Optional<UserDto> userO = userStorage.getUsers().stream()
                    .filter(userLoginIn -> registration.getLogin().equals(userLoginIn.getLogin())).findFirst();
            if (userO.isPresent()) {
                throw new LoggingException("User with that login present in system");
            }
            UserDto newUser = new UserDto();
            newUser.setLogin(registration.getLogin());
            newUser.setPassword(registration.getPassword());
            userStorage.getUsers().add(newUser);
            saveUser(userStorage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public boolean saveUserSettings(UserSettingsDto userSettings) {
        UserStorageDto userStorage = null;
        try {
            userStorage = loadUser();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Optional<UserDto> userO = userStorage.getUsers().stream()
                .filter(userLoginIn -> userSession.getLogin().equals(userLoginIn.getLogin())).findFirst();
        if (userO.isPresent()) {
            UserDto user = userO.get();
            user.getSettings().setBeloilUserCredential(userSettings.getBeloilUserCredential());
            user.getSettings().setYandexUserCredential(userSettings.getYandexUserCredential());
            try {
                saveUser(userStorage);
                userSession.getSettings().setYandexUserCredential(userSettings.getYandexUserCredential());
                userSession.getSettings().setBeloilUserCredential(userSettings.getBeloilUserCredential());
                return true;
            } catch (IOException e) {
                throw new RuntimeException("May not to update user settings");
            }
        }
        return false;
    }

    @Override
    public UserSessionDto getUserSession() {
        return userSession;
    }

    private UserStorageDto loadUser() throws IOException {
        return objectMapper.readValue(ResourceUtils.getFile(
                "classpath:data/user.json"), UserStorageDto.class);
    }

    private void saveUser(UserStorageDto userStorageDto) throws IOException {
        objectMapper.writeValue(ResourceUtils.getFile(
                "classpath:data/user.json"), userStorageDto);
    }
}
