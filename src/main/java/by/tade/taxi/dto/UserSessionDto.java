package by.tade.taxi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@AllArgsConstructor
@Data
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component("userSession")
public class UserSessionDto {
    private String login;
    private UserSettingsDto settings;

    public UserSessionDto() {
        this.settings = new UserSettingsDto();
    }
}
