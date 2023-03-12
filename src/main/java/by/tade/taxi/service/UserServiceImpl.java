package by.tade.taxi.service;

import by.tade.taxi.dto.UserDto;
import by.tade.taxi.dto.UserLoginDto;
import by.tade.taxi.dto.UserSessionDto;
import by.tade.taxi.dto.UserStorageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.Optional;

@Service
@Data
public class UserServiceImpl implements UserService {

    private final ObjectMapper objectMapper;
    private final UserSessionDto userSessionDto;
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
                userSessionDto.setBeloilUserCredential(user.getBeloilUserCredential());
                userSessionDto.setYandexUserCredential(user.getYandexUserCredential());
            return true;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private UserStorageDto loadUser()
            throws IOException {
        return objectMapper.readValue(ResourceUtils.getFile(
                "classpath:data/user.json"), UserStorageDto.class);
    }
}
