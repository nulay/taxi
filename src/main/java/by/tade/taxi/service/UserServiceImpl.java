package by.tade.taxi.service;

import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import by.tade.taxi.dto.DiscountGasDto;
import by.tade.taxi.dto.LinkCardWithYandexProfileRequestDto;
import by.tade.taxi.dto.RegistrationDto;
import by.tade.taxi.dto.UserDto;
import by.tade.taxi.dto.UserLoginDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.dto.UserSettingsDto;
import by.tade.taxi.dto.UserStorageDto;
import by.tade.taxi.dto.WriteOffGasTimeDto;
import by.tade.taxi.entity.LinkOilCardToYandexDriverEntity;
import by.tade.taxi.entity.UserTaxiEntity;
import by.tade.taxi.entity.repository.LinkOilCardToYandexRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Data
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    private final LinkOilCardToYandexRepository linkOilCardToYandexRepository;
    private final UserMapper userMapper;
    private final SchedulerService schedulerService;

    @Resource(name = "userSession")
    UserSessionDto userSession;

    @Override
    public boolean login(UserLoginDto userLogin) {
        Optional<UserTaxiEntity> userO = userRepository.getByLoginAndPassword(userLogin.getLogin(), userLogin.getPassword());
        if (userO.isPresent()) {
            UserTaxiEntity user = userO.get();
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
                user.setWriteOffGasTime(objectMapper.writeValueAsString(clean(userSettings.getWriteOffGasTime())));
                user.setDiscountGas(objectMapper.writeValueAsString(clean(userSettings.getDiscountGas())));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("May not parse object settings to String");
            }
            userRepository.save(user);
            restartScheduler(user);
            return true;
        }
        return false;
    }

    private void restartScheduler(UserTaxiEntity user) {
        if(user.getWriteOffGasTime() != null) {
            UserDto userDto = userMapper.toUserDto(user);
            if(schedulerService.cancel(userDto)) {
                schedulerService.add(userDto);
            }
        }
    }

    private List<DiscountGasDto> clean(List<DiscountGasDto> discountGas) {
        return discountGas.stream().filter(discount -> !discount.getPercent().equals("0") && !discount.getSumm().equals("0")).toList();
    }

    private WriteOffGasTimeDto clean(WriteOffGasTimeDto writeOffGasTime) {
        if (writeOffGasTime.getScheduler() == null || writeOffGasTime.getScheduler().equals("")) {
            return null;
        }
        if (WriteOffGasTimeDto.WEEK.equals(writeOffGasTime.getScheduler())) {
            writeOffGasTime.setMonthDay(null);
        }
        if (WriteOffGasTimeDto.MONTH.equals(writeOffGasTime.getScheduler())) {
            writeOffGasTime.setWeekEnum(null);
        }
        if (WriteOffGasTimeDto.DAY.equals(writeOffGasTime.getScheduler())) {
            writeOffGasTime.setMonthDay(null);
            writeOffGasTime.setWeekEnum(null);
        }
        return writeOffGasTime;
    }

    @Override
    public UserSessionDto getUserSession() {
        return userSession;
    }

    @Override
    public UserSettingsDto getUserSettings() {
        Optional<UserTaxiEntity> userO = userRepository.getByLogin(userSession.getLogin());
        if (userO.isPresent()) {
            UserDto userDto = userMapper.toUserDto(userO.get());
            return userDto.getSettings();
        }
        UserSettingsDto userSettings = new UserSettingsDto();
        userSettings.setYandexUserCredential(new YandexUserCredentialDto());
        userSettings.setBeloilUserCredential(new BeloilUserCredentialDto());
        userSettings.setDiscountGas(new ArrayList<>());
        userSettings.setWriteOffGasTime(new WriteOffGasTimeDto());
        return userSettings;
    }

    public UserStorageDto loadAllUsers() {
        return new UserStorageDto(userMapper.toUserStorageDto(userRepository.findAll()));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void linkCardWithYandexProfile(LinkCardWithYandexProfileRequestDto linkCardWithYandexProfileRequest) {
        Optional<UserTaxiEntity> userO = userRepository.getByLogin(userSession.getLogin());
        if(userO.isPresent()) {
            LinkOilCardToYandexDriverEntity linkOilCardToYandex;
            Optional<LinkOilCardToYandexDriverEntity> linkOilCardToYandexO =
                    linkOilCardToYandexRepository.getByCardCodeAndUserTaxiId(linkCardWithYandexProfileRequest.getCardCode(), userO.get().getId());
            if(linkOilCardToYandexO.isPresent()){
                linkOilCardToYandex = linkOilCardToYandexO.get();
            }else{
                linkOilCardToYandex = new LinkOilCardToYandexDriverEntity();
                linkOilCardToYandex.setUserTaxiId(userO.get().getId());
                linkOilCardToYandex.setCardCode(linkCardWithYandexProfileRequest.getCardCode());
            }
            linkOilCardToYandex.setYandexDriverProfile(linkCardWithYandexProfileRequest.getYandexDriverProfile());

            linkOilCardToYandexRepository.save(linkOilCardToYandex);
        }
    }

    public List<LinkOilCardToYandexDriverEntity> getLinkCardWithYandexProfile() {
        Optional<UserTaxiEntity> userO = userRepository.getByLogin(userSession.getLogin());
        if(userO.isPresent()) {
            List<LinkOilCardToYandexDriverEntity> linkOilCardToYandex = linkOilCardToYandexRepository.getAllByUserTaxiId(userO.get().getId());
            return linkOilCardToYandex;
        }
        return new ArrayList<>();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void deleteLinkCardWithYandexProfile(LinkCardWithYandexProfileRequestDto linkCardWithYandexProfileRequest) {
        Optional<UserTaxiEntity> userO = userRepository.getByLogin(userSession.getLogin());
        if(userO.isPresent()) {
            LinkOilCardToYandexDriverEntity linkOilCardToYandex =
                    linkOilCardToYandexRepository.getByUserTaxiIdAndCardCode(userO.get().getId(), linkCardWithYandexProfileRequest.getCardCode());
            linkOilCardToYandexRepository.delete(linkOilCardToYandex);
        }
    }
}
