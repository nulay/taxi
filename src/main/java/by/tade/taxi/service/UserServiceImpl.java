package by.tade.taxi.service;

import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import by.tade.taxi.dto.RegistrationDto;
import by.tade.taxi.dto.UserLoginDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.dto.UserSettingsDto;
import by.tade.taxi.dto.UserStorageDto;
import by.tade.taxi.entity.UserTaxiEntity;
import by.tade.taxi.entity.repository.UserRepository;
import by.tade.taxi.mapper.UserMapper;
import by.tade.taxi.yandex.dto.YandexUserCredentialDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.logging.log4j.LoggingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Data
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Resource(name = "userSession")
    UserSessionDto userSession;

    @Override
    public boolean login(UserLoginDto userLogin) {
        Optional<UserTaxiEntity> userO = userRepository.getByLoginAndPassword(userLogin.getLogin(), userLogin.getPassword());
        if (userO.isPresent()) {
            UserTaxiEntity user = userO.get();
            if (user.getBeloilCredential() != null) {
                BeloilUserCredentialDto beloilUserCredential = null;
                try {
                    beloilUserCredential = objectMapper.readValue(user.getBeloilCredential(),
                            BeloilUserCredentialDto.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Can not to parse beloilUserCredential");
                }
                userSession.getSettings().setBeloilUserCredential(beloilUserCredential);
            }
            if (user.getBeloilCredential() != null) {
                YandexUserCredentialDto yandexUserCredential = null;
                try {
                    yandexUserCredential = objectMapper.readValue(user.getYandexCredential(),
                            YandexUserCredentialDto.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Can not to parse yandexUserCredential");
                }
                userSession.getSettings().setYandexUserCredential(yandexUserCredential);
            }
            userSession.setLogin(user.getLogin());
            return true;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean registration(RegistrationDto registration) {
        if (!registration.getPassword().equals(registration.getPasswordRepeat())) {
            throw new LoggingException("Password and repeat password don't match");
        }
        Optional<UserTaxiEntity> userO = userRepository.getByLogin(registration.getLogin());
        if (userO.isPresent()) {
            throw new LoggingException("User with that login present in system");
        }

        UserTaxiEntity userTaxi = userMapper.toUserTaxi(registration);
        userTaxi.setEndActivationDate(LocalDate.now().plusMonths(1));
        userRepository.save(userTaxi);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean saveUserSettings(UserSettingsDto userSettings) {
        Optional<UserTaxiEntity> userO = userRepository.getByLogin(userSession.getLogin());
        if (userO.isPresent()) {
            UserTaxiEntity user = userO.get();
            try {
                user.setBeloilCredential(objectMapper.writeValueAsString(userSettings.getBeloilUserCredential()));
                user.setYandexCredential(objectMapper.writeValueAsString(userSettings.getYandexUserCredential()));
                user.setWriteOffGasTime(objectMapper.writeValueAsString(userSettings.getWriteOffGasTime()));
                user.setDiscountGas(objectMapper.writeValueAsString(userSettings.getDiscountGas()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("May not parse object settings to String");
            }
            userRepository.save(user);
            userSession.setSettings(userSettings);
            return true;

        }
        return false;
    }

    @Override
    public UserSessionDto getUserSession() {
        return userSession;
    }

    public UserStorageDto loadAllUsers() {
        return new UserStorageDto(userMapper.toUserStorageDto(userRepository.findAll()));
    }
}
