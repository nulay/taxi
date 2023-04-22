package by.tade.taxi.config;

import by.tade.taxi.entity.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@Slf4j
public class EventListenerSpringStartService {

    private final UserRepository userRepository;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Count user : {}", userRepository.count());

    }
}
