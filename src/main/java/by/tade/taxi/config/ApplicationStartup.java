package by.tade.taxi.config;

import by.tade.taxi.dto.UserStorageDto;
import by.tade.taxi.service.SchedulerService;
import by.tade.taxi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    public final SchedulerService schedulerService;
    private final UserService userService;

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        schedulerService.start(userService.loadAllUsers());
    }
}