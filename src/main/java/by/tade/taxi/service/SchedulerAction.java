package by.tade.taxi.service;

import by.tade.taxi.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Slf4j
public class SchedulerAction {

    private final UserDto user;

    public void executeOil() {
        if(user.getEndActivationDate().isBefore(LocalDate.now())){
            log.error("User activation is finished");
            throw new RuntimeException("User activation is finished");
        }
        System.out.println(user.getLogin());
    }
}
