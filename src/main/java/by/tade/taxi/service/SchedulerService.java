package by.tade.taxi.service;

import by.tade.taxi.dto.WriteOffGasTimeDto;
import by.tade.taxi.dto.UserDto;
import by.tade.taxi.dto.UserStorageDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class SchedulerService {

    private final ThreadPoolTaskScheduler poolTaskScheduler;
    private final Map<String, ScheduledFuture<?>> mapSchedulersOnLogin = new HashMap<>();

    public void start(UserStorageDto userStorage) {

        userStorage.getUsers().forEach(user -> add(user));
    }

    private void doSomething() {
    }


    public void add(UserDto userDto) {
        if (!validateTask(userDto)) return;

        SchedulerAction schedulerAction = new SchedulerAction(userDto);
        int min = 3;
        int max = 9;
        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);

        Runnable task = () -> schedulerAction.executeOil();
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
//
//        ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(task, random_int, random_int, TimeUnit.SECONDS);



//        00 01 * * THU - каждую среду в час ночи
        WriteOffGasTimeDto writeOffGasTime = userDto.getSettings().getWriteOffGasTime();

        String cronExpression = null;
        if (writeOffGasTime.getScheduler().contains(WriteOffGasTimeDto.DAY)) {
            cronExpression = "00 00 01 * * *"; // Каждый день в 1 час ночи
        }

        if (writeOffGasTime.getScheduler().contains(WriteOffGasTimeDto.WEEK)) {
            cronExpression = "0/"+random_int+" * * * * *";
//            cronExpression = "00 00 01 * * " + writeOffGasTime.getWeekEnum().getValue();// Каждую неделю в день WeekEnum в 1 час ночи
        }

        if (writeOffGasTime.getScheduler().contains(WriteOffGasTimeDto.MONTH)) {
            cronExpression = "00 00 01 * " + writeOffGasTime.getMonthDay() + " *"; // Каждый месяц в день MonthDay в 1 час ночи
        }

        if (cronExpression == null){
            return;
        }

        ScheduledFuture<?> scheduledFuture = poolTaskScheduler.schedule(task, new CronTrigger(cronExpression));
        mapSchedulersOnLogin.put(userDto.getLogin(), scheduledFuture);

        // Schedule a task that will be executed in 120 sec
//        ScheduledFuture<?> scheduledFuture = executor.schedule(task, 120, TimeUnit.SECONDS);


// Schedule a task that will be first run in 120 sec and each 120sec
// If an exception occurs then it's task executions are canceled.
//        executor.scheduleAtFixedRate(task, 120, 120, TimeUnit.SECONDS);

// Schedule a task that will be first run in 120 sec and each 120sec after the last execution
// If an exception occurs then it's task executions are canceled.
//        executor.scheduleWithFixedDelay(task, 120, 120, TimeUnit.SECONDS);
    }

    private boolean validateTask(UserDto userDto) {
        if (mapSchedulersOnLogin.get(userDto.getLogin()) != null) {
            log.error("This user task is started");
            return false;
        }
        if (userDto.getEndActivationDate() == null || userDto.getEndActivationDate().isBefore(LocalDate.now())) {
            log.error("User activation is finished");
            return false;
        }
        if (userDto.getSettings() == null || userDto.getSettings().getWriteOffGasTime() == null) {
            log.error("User settings is not right");
            return false;
        }
        return true;
    }

    public boolean cancel(UserDto userDto) {
        ScheduledFuture<?> scheduler = mapSchedulersOnLogin.get(userDto.getLogin());
        if (scheduler == null) {
            return true;
        }
        return scheduler.cancel(false);
    }
}
